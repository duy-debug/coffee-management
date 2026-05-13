package com.hoangtuan.coffee_management.service.impl;

import com.hoangtuan.coffee_management.dto.BaoCaoDoanhThuDTO;
import com.hoangtuan.coffee_management.dto.BaoCaoDoanhThuRowDTO;
import com.hoangtuan.coffee_management.dto.BaoCaoMonBanChayDTO;
import com.hoangtuan.coffee_management.dto.BaoCaoMonBanChayRowDTO;
import com.hoangtuan.coffee_management.dto.BaoCaoTonKhoDTO;
import com.hoangtuan.coffee_management.dto.BaoCaoTonKhoRowDTO;
import com.hoangtuan.coffee_management.dto.BaoCaoTongQuanDTO;
import com.hoangtuan.coffee_management.dto.DashboardDTO;
import com.hoangtuan.coffee_management.entity.ChiTietDonHang;
import com.hoangtuan.coffee_management.entity.HoaDon;
import com.hoangtuan.coffee_management.entity.Mon;
import com.hoangtuan.coffee_management.entity.NguyenLieu;
import com.hoangtuan.coffee_management.repository.ChiTietDonHangRepository;
import com.hoangtuan.coffee_management.repository.DonHangRepository;
import com.hoangtuan.coffee_management.repository.HoaDonRepository;
import com.hoangtuan.coffee_management.repository.MonRepository;
import com.hoangtuan.coffee_management.repository.NguyenLieuRepository;
import com.hoangtuan.coffee_management.repository.NhanVienRepository;
import com.hoangtuan.coffee_management.service.BaoCaoService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class BaoCaoServiceImpl implements BaoCaoService {

    private static final String TRANG_THAI_DA_THANH_TOAN = "Đã thanh toán";
    private static final String TRANG_THAI_DANG_BAN = "Đang bán";

    private final HoaDonRepository hoaDonRepository;
    private final DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final NguyenLieuRepository nguyenLieuRepository;
    private final MonRepository monRepository;
    private final NhanVienRepository nhanVienRepository;

    public BaoCaoServiceImpl(
            HoaDonRepository hoaDonRepository,
            DonHangRepository donHangRepository,
            ChiTietDonHangRepository chiTietDonHangRepository,
            NguyenLieuRepository nguyenLieuRepository,
            MonRepository monRepository,
            NhanVienRepository nhanVienRepository
    ) {
        this.hoaDonRepository = hoaDonRepository;
        this.donHangRepository = donHangRepository;
        this.chiTietDonHangRepository = chiTietDonHangRepository;
        this.nguyenLieuRepository = nguyenLieuRepository;
        this.monRepository = monRepository;
        this.nhanVienRepository = nhanVienRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public BaoCaoTongQuanDTO getTongQuanBaoCao() {
        DashboardDTO dashboard = getDashboard();
        return new BaoCaoTongQuanDTO(
                dashboard.getDoanhThuHomNay(),
                dashboard.getDoanhThuThangNay(),
                dashboard.getSoDonHangHomNay(),
                dashboard.getSoHoaDonDaThanhToan(),
                dashboard.getSoMonDangBan(),
                dashboard.getSoNguyenLieuSapHet(),
                dashboard.getSoNhanVienDangLam(),
                dashboard.getTongNguyenLieu()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public BaoCaoDoanhThuDTO getBaoCaoDoanhThu(LocalDate tuNgay, LocalDate denNgay) {
        LocalDate start = tuNgay != null ? tuNgay : LocalDate.now().withDayOfMonth(1);
        LocalDate end = denNgay != null ? denNgay : LocalDate.now();
        LocalDateTime tuNgayTime = start.atStartOfDay();
        LocalDateTime denNgayTime = end.atTime(LocalTime.MAX);

        List<BaoCaoDoanhThuRowDTO> rows = hoaDonRepository.findAll().stream()
                .filter(hoaDon -> isInRange(hoaDon.getNgayThanhToan(), tuNgayTime, denNgayTime))
                .sorted(Comparator.comparing(HoaDon::getNgayThanhToan).reversed())
                .map(this::toDoanhThuRow)
                .collect(Collectors.toList());

        BigDecimal tongDoanhThuTruocGiam = rows.stream()
                .map(BaoCaoDoanhThuRowDTO::getTongTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tongTienGiam = rows.stream()
                .map(BaoCaoDoanhThuRowDTO::getSoTienGiam)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tongDoanhThuThucThu = rows.stream()
                .map(BaoCaoDoanhThuRowDTO::getSoTienPhaiTra)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BaoCaoDoanhThuDTO(
                start,
                end,
                rows,
                rows.size(),
                scaleMoney(tongDoanhThuTruocGiam),
                scaleMoney(tongTienGiam),
                scaleMoney(tongDoanhThuThucThu)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public BaoCaoMonBanChayDTO getBaoCaoMonBanChay(LocalDate tuNgay, LocalDate denNgay) {
        LocalDate start = tuNgay != null ? tuNgay : LocalDate.now().withDayOfMonth(1);
        LocalDate end = denNgay != null ? denNgay : LocalDate.now();
        LocalDateTime tuNgayTime = start.atStartOfDay();
        LocalDateTime denNgayTime = end.atTime(LocalTime.MAX);

        Set<String> donHangDaThanhToan = hoaDonRepository.findAll().stream()
                .filter(hoaDon -> isInRange(hoaDon.getNgayThanhToan(), tuNgayTime, denNgayTime))
                .map(hoaDon -> hoaDon.getDonHang() != null ? hoaDon.getDonHang().getMaDonHang() : null)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());

        Map<String, MonSalesSummary> tongHop = new LinkedHashMap<>();
        for (ChiTietDonHang chiTiet : chiTietDonHangRepository.findAll()) {
            if (chiTiet.getDonHang() == null || !donHangDaThanhToan.contains(chiTiet.getDonHang().getMaDonHang())) {
                continue;
            }
            Mon mon = chiTiet.getMon();
            if (mon == null) {
                continue;
            }
            String maMon = mon.getMaMon();
            MonSalesSummary summary = tongHop.computeIfAbsent(maMon, key -> new MonSalesSummary(mon));
            summary.tongSoLuong += chiTiet.getSoLuong() == null ? 0 : chiTiet.getSoLuong();
            summary.tongDoanhThu = summary.tongDoanhThu.add(chiTiet.getThanhTien() == null ? BigDecimal.ZERO : chiTiet.getThanhTien());
        }

        List<BaoCaoMonBanChayRowDTO> rows = tongHop.values().stream()
                .sorted(Comparator.comparingLong(MonSalesSummary::getTongSoLuong).reversed()
                        .thenComparing(summary -> summary.mon.getTenMon(), Comparator.nullsLast(String::compareToIgnoreCase)))
                .map(summary -> new BaoCaoMonBanChayRowDTO(
                        summary.mon.getMaMon(),
                        summary.mon.getTenMon(),
                        summary.mon.getNhomMon() != null ? summary.mon.getNhomMon().getTenNhomMon() : "",
                        summary.tongSoLuong,
                        scaleMoney(summary.tongDoanhThu)
                ))
                .collect(Collectors.toList());

        long tongSoLuongBan = rows.stream().mapToLong(BaoCaoMonBanChayRowDTO::getTongSoLuongBan).sum();
        BigDecimal tongDoanhThuMon = rows.stream()
                .map(BaoCaoMonBanChayRowDTO::getTongDoanhThuMon)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BaoCaoMonBanChayDTO(start, end, rows, tongSoLuongBan, scaleMoney(tongDoanhThuMon));
    }

    @Override
    @Transactional(readOnly = true)
    public BaoCaoTonKhoDTO getBaoCaoTonKho() {
        List<BaoCaoTonKhoRowDTO> rows = nguyenLieuRepository.findAll().stream()
                .sorted(Comparator.comparing(NguyenLieu::getMaNguyenLieu))
                .map(this::toTonKhoRow)
                .collect(Collectors.toList());
        long tongNguyenLieuSapHet = rows.stream()
                .filter(row -> "Sắp hết".equalsIgnoreCase(row.getTrangThai()))
                .count();
        return new BaoCaoTonKhoDTO(rows, rows.size(), tongNguyenLieuSapHet);
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardDTO getDashboard() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = today.atStartOfDay();
        LocalDateTime endOfToday = today.atTime(LocalTime.MAX);
        LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();

        BigDecimal doanhThuHomNay = hoaDonRepository.findAll().stream()
                .filter(hoaDon -> isInRange(hoaDon.getNgayThanhToan(), startOfToday, endOfToday))
                .map(hoaDon -> hoaDon.getSoTienPhaiTra() == null ? BigDecimal.ZERO : hoaDon.getSoTienPhaiTra())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal doanhThuThangNay = hoaDonRepository.findAll().stream()
                .filter(hoaDon -> isInRange(hoaDon.getNgayThanhToan(), startOfMonth, endOfToday))
                .map(hoaDon -> hoaDon.getSoTienPhaiTra() == null ? BigDecimal.ZERO : hoaDon.getSoTienPhaiTra())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long soDonHangHomNay = donHangRepository.findAll().stream()
                .filter(donHang -> isInRange(donHang.getNgayDat(), startOfToday, endOfToday))
                .count();

        long soHoaDonDaThanhToan = hoaDonRepository.count();
        long soMonDangBan = monRepository.findByTrangThai(Boolean.TRUE).size();
        long soNguyenLieuSapHet = nguyenLieuRepository.findAll().stream()
                .filter(this::isSapHet)
                .count();
        long soNhanVienDangLam = nhanVienRepository.findByTrangThai(Boolean.TRUE).size();
        long tongNguyenLieu = nguyenLieuRepository.count();

        return new DashboardDTO(
                scaleMoney(doanhThuHomNay),
                scaleMoney(doanhThuThangNay),
                soDonHangHomNay,
                soHoaDonDaThanhToan,
                soMonDangBan,
                soNguyenLieuSapHet,
                soNhanVienDangLam,
                tongNguyenLieu
        );
    }

    private BaoCaoDoanhThuRowDTO toDoanhThuRow(HoaDon hoaDon) {
        return new BaoCaoDoanhThuRowDTO(
                hoaDon.getMaHoaDon(),
                hoaDon.getNgayThanhToan(),
                hoaDon.getDonHang() != null ? hoaDon.getDonHang().getMaDonHang() : "",
                hoaDon.getPhuongThucThanhToan(),
                scaleMoney(hoaDon.getTongTien()),
                scaleMoney(hoaDon.getSoTienGiam()),
                scaleMoney(hoaDon.getSoTienPhaiTra())
        );
    }

    private BaoCaoTonKhoRowDTO toTonKhoRow(NguyenLieu nguyenLieu) {
        String trangThai = isSapHet(nguyenLieu) ? "Sắp hết" : "Đủ hàng";
        return new BaoCaoTonKhoRowDTO(
                nguyenLieu.getMaNguyenLieu(),
                nguyenLieu.getTenNguyenLieu(),
                nguyenLieu.getDonViTinh(),
                nguyenLieu.getSoLuongTon(),
                nguyenLieu.getMucTonToiThieu(),
                trangThai
        );
    }

    private boolean isSapHet(NguyenLieu nguyenLieu) {
        if (nguyenLieu == null || nguyenLieu.getSoLuongTon() == null || nguyenLieu.getMucTonToiThieu() == null) {
            return false;
        }
        return nguyenLieu.getSoLuongTon() <= nguyenLieu.getMucTonToiThieu();
    }

    private boolean isInRange(LocalDateTime time, LocalDateTime start, LocalDateTime end) {
        if (time == null || start == null || end == null) {
            return false;
        }
        return !time.isBefore(start) && !time.isAfter(end);
    }

    private BigDecimal scaleMoney(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private static class MonSalesSummary {
        private final Mon mon;
        private long tongSoLuong;
        private BigDecimal tongDoanhThu = BigDecimal.ZERO;

        private MonSalesSummary(Mon mon) {
            this.mon = mon;
        }

        private long getTongSoLuong() {
            return tongSoLuong;
        }
    }
}
