package com.hoangtuan.coffee_management.service.impl;

import com.hoangtuan.coffee_management.dto.BanHangItemDTO;
import com.hoangtuan.coffee_management.dto.BanHangRequestDTO;
import com.hoangtuan.coffee_management.entity.Ban;
import com.hoangtuan.coffee_management.entity.ChiTietDonHang;
import com.hoangtuan.coffee_management.entity.DonHang;
import com.hoangtuan.coffee_management.entity.KhachHang;
import com.hoangtuan.coffee_management.entity.Mon;
import com.hoangtuan.coffee_management.entity.NhanVien;
import com.hoangtuan.coffee_management.entity.TaiKhoan;
import com.hoangtuan.coffee_management.repository.BanRepository;
import com.hoangtuan.coffee_management.repository.ChiTietDonHangRepository;
import com.hoangtuan.coffee_management.repository.DonHangRepository;
import com.hoangtuan.coffee_management.repository.KhachHangRepository;
import com.hoangtuan.coffee_management.repository.MonRepository;
import com.hoangtuan.coffee_management.repository.TaiKhoanRepository;
import com.hoangtuan.coffee_management.service.DonHangService;
import com.hoangtuan.coffee_management.util.CodeGeneratorUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class DonHangServiceImpl implements DonHangService {

    private final DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final BanRepository banRepository;
    private final KhachHangRepository khachHangRepository;
    private final MonRepository monRepository;

    public DonHangServiceImpl(
            DonHangRepository donHangRepository,
            ChiTietDonHangRepository chiTietDonHangRepository,
            TaiKhoanRepository taiKhoanRepository,
            BanRepository banRepository,
            KhachHangRepository khachHangRepository,
            MonRepository monRepository
    ) {
        this.donHangRepository = donHangRepository;
        this.chiTietDonHangRepository = chiTietDonHangRepository;
        this.taiKhoanRepository = taiKhoanRepository;
        this.banRepository = banRepository;
        this.khachHangRepository = khachHangRepository;
        this.monRepository = monRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DonHang findById(String maDonHang) {
        return donHangRepository.findById(maDonHang)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay don hang: " + maDonHang));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonHang> findAll(String keyword, String trangThai, String loaiDonHang, LocalDate tuNgay, LocalDate denNgay) {
        List<DonHang> donHangs = donHangRepository.findAll(Sort.by(Sort.Direction.DESC, "ngayDat"));

        if (StringUtils.hasText(keyword)) {
            String normalizedKeyword = keyword.trim().toLowerCase();
            donHangs = donHangs.stream()
                    .filter(donHang -> matchesKeyword(donHang, normalizedKeyword))
                    .collect(Collectors.toList());
        }
        if (StringUtils.hasText(trangThai)) {
            String normalizedTrangThai = normalize(trangThai);
            donHangs = donHangs.stream()
                    .filter(donHang -> normalizedTrangThai.equalsIgnoreCase(donHang.getTrangThai()))
                    .collect(Collectors.toList());
        }
        if (StringUtils.hasText(loaiDonHang)) {
            String normalizedLoai = normalize(loaiDonHang);
            donHangs = donHangs.stream()
                    .filter(donHang -> normalizedLoai.equalsIgnoreCase(donHang.getLoaiDonHang()))
                    .collect(Collectors.toList());
        }
        if (tuNgay != null) {
            LocalDateTime start = tuNgay.atStartOfDay();
            donHangs = donHangs.stream()
                    .filter(donHang -> !donHang.getNgayDat().isBefore(start))
                    .collect(Collectors.toList());
        }
        if (denNgay != null) {
            LocalDateTime end = denNgay.atTime(LocalTime.MAX);
            donHangs = donHangs.stream()
                    .filter(donHang -> !donHang.getNgayDat().isAfter(end))
                    .collect(Collectors.toList());
        }
        return donHangs;
    }

    @Override
    @Transactional
    public DonHang createDonHang(BanHangRequestDTO requestDTO, String tenDangNhap, List<BanHangItemDTO> gioHang) {
        validateGioHang(gioHang);
        validateRequest(requestDTO);

        TaiKhoan taiKhoan = taiKhoanRepository.findByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay tai khoan dang dang nhap."));
        NhanVien nhanVien = taiKhoan.getNhanVien();
        if (nhanVien == null) {
            throw new IllegalArgumentException("Tai khoan chua gan voi nhan vien.");
        }

        String loaiDonHang = normalize(requestDTO.getLoaiDonHang());
        String maBan = normalize(requestDTO.getMaBan());
        String maKhachHang = normalize(requestDTO.getMaKhachHang());
        KhachHang khachHang = null;
        if (StringUtils.hasText(maKhachHang)) {
            khachHang = khachHangRepository.findById(maKhachHang)
                    .orElseThrow(() -> new IllegalArgumentException("Khong tim thay khach hang."));
        }

        DonHang donHang = new DonHang();
        donHang.setMaDonHang(generateNextMaDonHang());
        donHang.setNgayDat(LocalDateTime.now());
        donHang.setLoaiDonHang(loaiDonHang);
        donHang.setNhanVien(nhanVien);
        donHang.setKhachHang(khachHang);
        donHang.setTongTien(tinhTongTien(gioHang));

        if ("Dùng tại quán".equalsIgnoreCase(loaiDonHang)) {
            if (!StringUtils.hasText(maBan)) {
                throw new IllegalArgumentException("Vui long chon ban cho don dung tai quan.");
            }
            Ban ban = banRepository.findById(maBan)
                    .orElseThrow(() -> new IllegalArgumentException("Khong tim thay ban."));
            if (!"Trống".equalsIgnoreCase(ban.getTrangThai())) {
                throw new IllegalArgumentException("Chi duoc chon ban co trang thai Trong.");
            }
            ban.setTrangThai("Đang phục vụ");
            banRepository.save(ban);
            donHang.setBan(ban);
            donHang.setTrangThai("Đang phục vụ");
        } else {
            donHang.setBan(null);
            donHang.setTrangThai("Chờ thanh toán");
        }

        donHangRepository.save(donHang);

        for (BanHangItemDTO item : gioHang) {
            Mon mon = monRepository.findById(item.getMaMon())
                    .orElseThrow(() -> new IllegalArgumentException("Khong tim thay mon: " + item.getMaMon()));
            if (Boolean.FALSE.equals(mon.getTrangThai())) {
                throw new IllegalArgumentException("Khong the tao don voi mon ngung ban: " + mon.getTenMon());
            }

            ChiTietDonHang chiTiet = new ChiTietDonHang();
            chiTiet.setMaCTDH(generateNextMaCTDH());
            chiTiet.setDonHang(donHang);
            chiTiet.setMon(mon);
            chiTiet.setSoLuong(item.getSoLuong());
            chiTiet.setDonGia(item.getDonGia());
            chiTiet.setThanhTien(item.getThanhTien());
            chiTietDonHangRepository.save(chiTiet);
        }

        return donHang;
    }

    @Override
    @Transactional
    public void updateTongTien(String maDonHang) {
        DonHang donHang = findById(maDonHang);
        List<ChiTietDonHang> chiTietDonHangs = chiTietDonHangRepository.findByDonHang_MaDonHang(maDonHang);
        BigDecimal tongTien = chiTietDonHangs.stream()
                .map(ChiTietDonHang::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        donHang.setTongTien(tongTien);
        donHangRepository.save(donHang);
        dongBoTrangThaiBan(donHang);
    }

    @Override
    @Transactional
    public void updateTrangThai(String maDonHang, String trangThai) {
        DonHang donHang = findById(maDonHang);
        donHang.setTrangThai(validateTrangThai(trangThai));
        donHangRepository.save(donHang);
        dongBoTrangThaiBan(donHang);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChiTietDonHang> findChiTietByDonHang(String maDonHang) {
        return chiTietDonHangRepository.findByDonHang_MaDonHang(maDonHang);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean kiemTraCoTheSua(DonHang donHang) {
        if (donHang == null || !StringUtils.hasText(donHang.getTrangThai())) {
            return false;
        }
        return !"Đã thanh toán".equalsIgnoreCase(donHang.getTrangThai())
                && !"Đã hủy".equalsIgnoreCase(donHang.getTrangThai());
    }

    @Override
    @Transactional
    public void themMon(String maDonHang, String maMon, Integer soLuong) {
        DonHang donHang = findEditableDonHang(maDonHang);
        if (soLuong == null || soLuong <= 0) {
            throw new IllegalArgumentException("So luong phai lon hon 0.");
        }

        Mon mon = monRepository.findById(maMon)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay mon."));
        if (Boolean.FALSE.equals(mon.getTrangThai())) {
            throw new IllegalArgumentException("Khong the them mon ngung ban.");
        }

        List<ChiTietDonHang> chiTietDonHangs = new ArrayList<>(chiTietDonHangRepository.findByDonHang_MaDonHang(maDonHang));
        ChiTietDonHang existing = chiTietDonHangs.stream()
                .filter(item -> item.getMon() != null && item.getMon().getMaMon().equalsIgnoreCase(maMon))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            existing.setSoLuong(existing.getSoLuong() + soLuong);
            existing.setThanhTien(existing.getDonGia().multiply(BigDecimal.valueOf(existing.getSoLuong())));
            chiTietDonHangRepository.save(existing);
        } else {
            ChiTietDonHang chiTiet = new ChiTietDonHang();
            chiTiet.setMaCTDH(generateNextMaCTDH());
            chiTiet.setDonHang(donHang);
            chiTiet.setMon(mon);
            chiTiet.setSoLuong(soLuong);
            chiTiet.setDonGia(mon.getDonGia());
            chiTiet.setThanhTien(mon.getDonGia().multiply(BigDecimal.valueOf(soLuong)));
            chiTietDonHangRepository.save(chiTiet);
        }

        capNhatTongTien(maDonHang);
        capNhatTrangThaiSauChinhSua(donHang);
    }

    @Override
    @Transactional
    public void capNhatSoLuong(String maCTDH, Integer soLuong) {
        if (soLuong == null || soLuong <= 0) {
            throw new IllegalArgumentException("So luong phai lon hon 0.");
        }
        ChiTietDonHang chiTiet = chiTietDonHangRepository.findById(maCTDH)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay chi tiet don hang."));
        DonHang donHang = findEditableDonHang(chiTiet.getDonHang().getMaDonHang());

        chiTiet.setSoLuong(soLuong);
        chiTiet.setThanhTien(chiTiet.getDonGia().multiply(BigDecimal.valueOf(soLuong)));
        chiTietDonHangRepository.save(chiTiet);

        capNhatTongTien(donHang.getMaDonHang());
        capNhatTrangThaiSauChinhSua(donHang);
    }

    @Override
    @Transactional
    public void xoaMon(String maCTDH) {
        ChiTietDonHang chiTiet = chiTietDonHangRepository.findById(maCTDH)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay chi tiet don hang."));
        String maDonHang = chiTiet.getDonHang().getMaDonHang();
        DonHang donHang = findEditableDonHang(maDonHang);

        List<ChiTietDonHang> chiTietDonHangs = chiTietDonHangRepository.findByDonHang_MaDonHang(maDonHang);
        if (chiTietDonHangs.size() <= 1) {
            throw new IllegalArgumentException("Khong the xoa het mon cua don hang. Hay huy don neu khong can giu don.");
        }

        chiTietDonHangRepository.delete(chiTiet);
        capNhatTongTien(maDonHang);
        capNhatTrangThaiSauChinhSua(donHang);
    }

    @Override
    @Transactional
    public void capNhatTrangThai(String maDonHang, String trangThai) {
        DonHang donHang = findEditableDonHang(maDonHang);
        String normalized = validateTrangThai(trangThai);
        donHang.setTrangThai(normalized);
        donHangRepository.save(donHang);
        dongBoTrangThaiBan(donHang);
    }

    @Override
    @Transactional
    public void huyDon(String maDonHang) {
        DonHang donHang = findById(maDonHang);
        if (!kiemTraCoTheSua(donHang)) {
            throw new IllegalArgumentException("Khong the huy don da thanh toan hoac da huy.");
        }
        donHang.setTrangThai("Đã hủy");
        donHangRepository.save(donHang);
        dongBoTrangThaiBan(donHang);
    }

    @Override
    @Transactional
    public void capNhatTongTien(String maDonHang) {
        DonHang donHang = findById(maDonHang);
        List<ChiTietDonHang> chiTietDonHangs = chiTietDonHangRepository.findByDonHang_MaDonHang(maDonHang);
        BigDecimal tongTien = chiTietDonHangs.stream()
                .map(ChiTietDonHang::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        donHang.setTongTien(tongTien);
        donHangRepository.save(donHang);
    }

    @Override
    @Transactional(readOnly = true)
    public String findMaDonHangByChiTiet(String maCTDH) {
        return chiTietDonHangRepository.findById(maCTDH)
                .map(chiTiet -> chiTiet.getDonHang().getMaDonHang())
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay chi tiet don hang."));
    }

    private void validateGioHang(List<BanHangItemDTO> gioHang) {
        if (gioHang == null || gioHang.isEmpty()) {
            throw new IllegalArgumentException("Gio hang khong duoc de trong.");
        }
        for (BanHangItemDTO item : gioHang) {
            if (item == null || !StringUtils.hasText(item.getMaMon())) {
                throw new IllegalArgumentException("Co mon khong hop le trong gio hang.");
            }
            if (item.getSoLuong() == null || item.getSoLuong() <= 0) {
                throw new IllegalArgumentException("So luong mon phai lon hon 0.");
            }
            if (item.getDonGia() == null || item.getDonGia().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Don gia mon khong hop le.");
            }
        }
    }

    private void validateRequest(BanHangRequestDTO requestDTO) {
        if (requestDTO == null || !StringUtils.hasText(requestDTO.getLoaiDonHang())) {
            throw new IllegalArgumentException("Vui long chon loai don hang.");
        }
        if (!"Dùng tại quán".equalsIgnoreCase(requestDTO.getLoaiDonHang())
                && !"Mang đi".equalsIgnoreCase(requestDTO.getLoaiDonHang())) {
            throw new IllegalArgumentException("Loai don hang khong hop le.");
        }
    }

    private BigDecimal tinhTongTien(List<BanHangItemDTO> gioHang) {
        return gioHang.stream()
                .map(BanHangItemDTO::getThanhTien)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String generateNextMaDonHang() {
        return CodeGeneratorUtil.generateNextCode(
                "DH",
                donHangRepository.findAll().stream().map(DonHang::getMaDonHang).collect(Collectors.toList()),
                3
        );
    }

    private String generateNextMaCTDH() {
        return CodeGeneratorUtil.generateNextCode(
                "CTDH",
                chiTietDonHangRepository.findAll().stream().map(ChiTietDonHang::getMaCTDH).collect(Collectors.toList()),
                3
        );
    }

    private String validateTrangThai(String trangThai) {
        if (!StringUtils.hasText(trangThai)) {
            throw new IllegalArgumentException("Trang thai don hang khong duoc de trong.");
        }
        String normalized = normalize(trangThai);
        if (!List.of("Đang xử lý", "Đang phục vụ", "Chờ thanh toán", "Đã hủy").contains(normalized)) {
            throw new IllegalArgumentException("Trang thai don hang khong hop le.");
        }
        return normalized;
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private boolean matchesKeyword(DonHang donHang, String keyword) {
        return (donHang.getMaDonHang() != null && donHang.getMaDonHang().toLowerCase().contains(keyword))
                || (donHang.getLoaiDonHang() != null && donHang.getLoaiDonHang().toLowerCase().contains(keyword))
                || (donHang.getTrangThai() != null && donHang.getTrangThai().toLowerCase().contains(keyword))
                || (donHang.getNhanVien() != null && donHang.getNhanVien().getHoTen() != null && donHang.getNhanVien().getHoTen().toLowerCase().contains(keyword))
                || (donHang.getKhachHang() != null && donHang.getKhachHang().getHoTen() != null && donHang.getKhachHang().getHoTen().toLowerCase().contains(keyword))
                || (donHang.getBan() != null && donHang.getBan().getTenBan() != null && donHang.getBan().getTenBan().toLowerCase().contains(keyword));
    }

    private DonHang findEditableDonHang(String maDonHang) {
        DonHang donHang = findById(maDonHang);
        if (!kiemTraCoTheSua(donHang)) {
            throw new IllegalArgumentException("Don hang da thanh toan hoac da huy khong the chinh sua.");
        }
        return donHang;
    }

    private void capNhatTrangThaiSauChinhSua(DonHang donHang) {
        donHang.setTrangThai("Chờ thanh toán");
        donHangRepository.save(donHang);
        dongBoTrangThaiBan(donHang);
    }

    private void dongBoTrangThaiBan(DonHang donHang) {
        if (donHang.getBan() == null) {
            return;
        }
        Ban ban = donHang.getBan();
        if ("Đã hủy".equalsIgnoreCase(donHang.getTrangThai())) {
            ban.setTrangThai("Trống");
        } else if ("Chờ thanh toán".equalsIgnoreCase(donHang.getTrangThai())) {
            ban.setTrangThai("Chờ thanh toán");
        } else if ("Đang phục vụ".equalsIgnoreCase(donHang.getTrangThai())) {
            ban.setTrangThai("Đang phục vụ");
        } else if ("Đang xử lý".equalsIgnoreCase(donHang.getTrangThai())) {
            ban.setTrangThai("Trống");
        }
        banRepository.save(ban);
    }
}
