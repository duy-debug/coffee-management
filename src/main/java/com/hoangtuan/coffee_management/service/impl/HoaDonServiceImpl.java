package com.hoangtuan.coffee_management.service.impl;

import com.hoangtuan.coffee_management.dto.HoaDonThanhToanViewDTO;
import com.hoangtuan.coffee_management.dto.ThanhToanDTO;
import com.hoangtuan.coffee_management.entity.Ban;
import com.hoangtuan.coffee_management.entity.ChiTietDonHang;
import com.hoangtuan.coffee_management.entity.DonHang;
import com.hoangtuan.coffee_management.entity.HoaDon;
import com.hoangtuan.coffee_management.entity.KhuyenMai;
import com.hoangtuan.coffee_management.entity.NhanVien;
import com.hoangtuan.coffee_management.entity.TaiKhoan;
import com.hoangtuan.coffee_management.repository.ChiTietDonHangRepository;
import com.hoangtuan.coffee_management.repository.DonHangRepository;
import com.hoangtuan.coffee_management.repository.HoaDonRepository;
import com.hoangtuan.coffee_management.repository.TaiKhoanRepository;
import com.hoangtuan.coffee_management.service.BanService;
import com.hoangtuan.coffee_management.service.DonHangService;
import com.hoangtuan.coffee_management.service.HoaDonService;
import com.hoangtuan.coffee_management.service.KhuyenMaiService;
import com.hoangtuan.coffee_management.util.CodeGeneratorUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class HoaDonServiceImpl implements HoaDonService {

    private static final Set<String> PHUONG_THUC_HOP_LE = Set.of("Tiền mặt", "Chuyển khoản", "Ví điện tử");
    private static final Set<String> TRANG_THAI_CHO_THANH_TOAN = Set.of("Đang xử lý", "Đang phục vụ", "Chờ thanh toán");

    private final HoaDonRepository hoaDonRepository;
    private final DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final DonHangService donHangService;
    private final BanService banService;
    private final KhuyenMaiService khuyenMaiService;

    public HoaDonServiceImpl(
            HoaDonRepository hoaDonRepository,
            DonHangRepository donHangRepository,
            ChiTietDonHangRepository chiTietDonHangRepository,
            TaiKhoanRepository taiKhoanRepository,
            DonHangService donHangService,
            BanService banService,
            KhuyenMaiService khuyenMaiService
    ) {
        this.hoaDonRepository = hoaDonRepository;
        this.donHangRepository = donHangRepository;
        this.chiTietDonHangRepository = chiTietDonHangRepository;
        this.taiKhoanRepository = taiKhoanRepository;
        this.donHangService = donHangService;
        this.banService = banService;
        this.khuyenMaiService = khuyenMaiService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoaDon> findAll(String keyword, String phuongThucThanhToan, LocalDate tuNgay, LocalDate denNgay) {
        String search = normalize(keyword);
        String phuongThuc = normalize(phuongThucThanhToan);
        LocalDateTime tuNgayTime = tuNgay != null ? tuNgay.atStartOfDay() : null;
        LocalDateTime denNgayTime = denNgay != null ? denNgay.plusDays(1).atStartOfDay().minusNanos(1) : null;

        return hoaDonRepository.findAll().stream()
                .filter(hoaDon -> !StringUtils.hasText(search)
                        || safeString(hoaDon.getMaHoaDon()).toLowerCase().contains(search.toLowerCase())
                        || (hoaDon.getDonHang() != null && safeString(hoaDon.getDonHang().getMaDonHang()).toLowerCase().contains(search.toLowerCase())))
                .filter(hoaDon -> !StringUtils.hasText(phuongThuc)
                        || "all".equalsIgnoreCase(phuongThuc)
                        || safeString(hoaDon.getPhuongThucThanhToan()).equalsIgnoreCase(phuongThuc))
                .filter(hoaDon -> tuNgayTime == null || !hoaDon.getNgayThanhToan().isBefore(tuNgayTime))
                .filter(hoaDon -> denNgayTime == null || !hoaDon.getNgayThanhToan().isAfter(denNgayTime))
                .sorted((left, right) -> right.getNgayThanhToan().compareTo(left.getNgayThanhToan()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HoaDon findById(String maHoaDon) {
        return hoaDonRepository.findById(maHoaDon)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay hoa don: " + maHoaDon));
    }

    @Override
    @Transactional(readOnly = true)
    public HoaDon findByDonHang(String maDonHang) {
        return hoaDonRepository.findByDonHang_MaDonHang(maDonHang)
                .orElseThrow(() -> new IllegalArgumentException("Don hang chua duoc thanh toan."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonHang> findDonHangChoThanhToan() {
        return donHangRepository.findAll().stream()
                .filter(donHang -> TRANG_THAI_CHO_THANH_TOAN.contains(normalize(donHang.getTrangThai())))
                .sorted((left, right) -> right.getNgayDat().compareTo(left.getNgayDat()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HoaDonThanhToanViewDTO getThanhToanData(String maDonHang, String maKhuyenMai, String phuongThucThanhToan) {
        DonHang donHang = findDonHangHopLe(maDonHang);
        List<ChiTietDonHang> chiTietDonHangs = chiTietDonHangRepository.findByDonHang_MaDonHang(maDonHang);
        List<KhuyenMai> danhSachKhuyenMai = khuyenMaiService.findKhuyenMaiConHieuLuc();
        KhuyenMai khuyenMai = resolveKhuyenMai(maKhuyenMai, danhSachKhuyenMai);
        BigDecimal tongTien = normalizeMoney(donHang.getTongTien());
        BigDecimal soTienGiam = tinhTienGiam(tongTien, khuyenMai);
        BigDecimal soTienPhaiTra = tongTien.subtract(soTienGiam).max(BigDecimal.ZERO);

        return new HoaDonThanhToanViewDTO(
                donHang,
                chiTietDonHangs,
                danhSachKhuyenMai,
                tongTien,
                soTienGiam,
                soTienPhaiTra,
                StringUtils.hasText(phuongThucThanhToan) ? phuongThucThanhToan : "Tiền mặt",
                StringUtils.hasText(maKhuyenMai) ? maKhuyenMai.trim() : ""
        );
    }

    @Override
    @Transactional
    public HoaDon thanhToan(String maDonHang, ThanhToanDTO dto, String tenDangNhap) {
        DonHang donHang = findDonHangHopLe(maDonHang);
        if (hoaDonRepository.findByDonHang_MaDonHang(maDonHang).isPresent()) {
            throw new IllegalArgumentException("Don hang nay da co hoa don.");
        }
        if (dto == null || !StringUtils.hasText(dto.getPhuongThucThanhToan())) {
            throw new IllegalArgumentException("Vui long chon phuong thuc thanh toan.");
        }
        String phuongThuc = normalizePhuongThuc(dto.getPhuongThucThanhToan());
        List<KhuyenMai> danhSachKhuyenMai = khuyenMaiService.findKhuyenMaiConHieuLuc();
        KhuyenMai khuyenMai = resolveKhuyenMai(dto.getMaKhuyenMai(), danhSachKhuyenMai);

        TaiKhoan taiKhoan = taiKhoanRepository.findByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay tai khoan dang dang nhap."));
        NhanVien nhanVien = taiKhoan.getNhanVien();
        if (nhanVien == null) {
            throw new IllegalArgumentException("Tai khoan chua gan voi nhan vien.");
        }

        BigDecimal tongTien = normalizeMoney(donHang.getTongTien());
        BigDecimal soTienGiam = tinhTienGiam(tongTien, khuyenMai);
        BigDecimal soTienPhaiTra = tongTien.subtract(soTienGiam).max(BigDecimal.ZERO);

        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHoaDon(generateNextMaHoaDon());
        hoaDon.setNgayThanhToan(LocalDateTime.now());
        hoaDon.setPhuongThucThanhToan(phuongThuc);
        hoaDon.setTongTien(tongTien);
        hoaDon.setSoTienGiam(soTienGiam);
        hoaDon.setSoTienPhaiTra(soTienPhaiTra);
        hoaDon.setDonHang(donHang);
        hoaDon.setKhuyenMai(khuyenMai);
        hoaDonRepository.save(hoaDon);

        donHangService.updateTrangThai(maDonHang, "Đã thanh toán");
        if (donHang.getBan() != null) {
            Ban ban = donHang.getBan();
            ban.setTrangThai(BanServiceImpl.TRONG);
            banService.updateTrangThai(ban.getMaBan(), BanServiceImpl.TRONG);
        }

        return hoaDon;
    }

    @Override
    public BigDecimal tinhTienGiam(BigDecimal tongTien, KhuyenMai khuyenMai) {
        if (tongTien == null || tongTien.compareTo(BigDecimal.ZERO) <= 0 || khuyenMai == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal soTienGiam;
        if ("GIAM_TIEN".equalsIgnoreCase(normalize(khuyenMai.getLoaiKhuyenMai()))) {
            soTienGiam = normalizeMoney(khuyenMai.getGiaTriGiam());
        } else if ("GIAM_PHAN_TRAM".equalsIgnoreCase(normalize(khuyenMai.getLoaiKhuyenMai()))) {
            soTienGiam = tongTien.multiply(normalizeMoney(khuyenMai.getGiaTriGiam()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            soTienGiam = BigDecimal.ZERO;
        }
        return soTienGiam.min(tongTien).max(BigDecimal.ZERO);
    }

    @Override
    @Transactional(readOnly = true)
    public String generateNextMaHoaDon() {
        return CodeGeneratorUtil.generateNextCode(
                "HD",
                hoaDonRepository.findAll().stream().map(HoaDon::getMaHoaDon).collect(Collectors.toList()),
                3
        );
    }

    private DonHang findDonHangHopLe(String maDonHang) {
        DonHang donHang = donHangRepository.findById(maDonHang)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay don hang: " + maDonHang));
        if (hoaDonRepository.findByDonHang_MaDonHang(maDonHang).isPresent()) {
            throw new IllegalArgumentException("Don hang nay da duoc thanh toan.");
        }
        if ("Đã hủy".equalsIgnoreCase(normalize(donHang.getTrangThai()))) {
            throw new IllegalArgumentException("Khong the thanh toan don hang da huy.");
        }
        if (chiTietDonHangRepository.findByDonHang_MaDonHang(maDonHang).isEmpty()) {
            throw new IllegalArgumentException("Don hang khong co mon.");
        }
        return donHang;
    }

    private KhuyenMai resolveKhuyenMai(String maKhuyenMai, List<KhuyenMai> danhSachKhuyenMai) {
        if (!StringUtils.hasText(maKhuyenMai)) {
            return null;
        }
        String ma = maKhuyenMai.trim();
        return danhSachKhuyenMai.stream()
                .filter(khuyenMai -> ma.equalsIgnoreCase(khuyenMai.getMaKhuyenMai()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Khuyen mai khong hop le hoac da het hieu luc."));
    }

    private String normalizePhuongThuc(String phuongThuc) {
        String normalized = normalize(phuongThuc);
        if (!PHUONG_THUC_HOP_LE.contains(normalized)) {
            throw new IllegalArgumentException("Phuong thuc thanh toan khong hop le.");
        }
        return normalized;
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }

    private BigDecimal normalizeMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
