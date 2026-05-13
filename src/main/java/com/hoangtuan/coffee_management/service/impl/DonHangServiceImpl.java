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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng: " + maDonHang));
    }

    @Override
    @Transactional
    public DonHang createDonHang(BanHangRequestDTO requestDTO, String tenDangNhap, List<BanHangItemDTO> gioHang) {
        validateGioHang(gioHang);
        validateRequest(requestDTO);

        TaiKhoan taiKhoan = taiKhoanRepository.findByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản đang đăng nhập."));
        NhanVien nhanVien = taiKhoan.getNhanVien();
        if (nhanVien == null) {
            throw new IllegalArgumentException("Tài khoản chưa gắn với nhân viên.");
        }

        String loaiDonHang = normalize(requestDTO.getLoaiDonHang());
        String maBan = normalize(requestDTO.getMaBan());
        String maKhachHang = normalize(requestDTO.getMaKhachHang());
        KhachHang khachHang = null;
        if (StringUtils.hasText(maKhachHang)) {
            khachHang = khachHangRepository.findById(maKhachHang)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng."));
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
                throw new IllegalArgumentException("Vui lòng chọn bàn cho đơn dùng tại quán.");
            }
            Ban ban = banRepository.findById(maBan)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bàn."));
            if (!"Trống".equalsIgnoreCase(ban.getTrangThai())) {
                throw new IllegalArgumentException("Chỉ được chọn bàn có trạng thái Trống.");
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
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy món: " + item.getMaMon()));
            if (Boolean.FALSE.equals(mon.getTrangThai())) {
                throw new IllegalArgumentException("Không thể tạo đơn với món ngừng bán: " + mon.getTenMon());
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
    }

    @Override
    @Transactional
    public void updateTrangThai(String maDonHang, String trangThai) {
        DonHang donHang = findById(maDonHang);
        donHang.setTrangThai(validateTrangThai(trangThai));
        donHangRepository.save(donHang);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChiTietDonHang> findChiTietByDonHang(String maDonHang) {
        return chiTietDonHangRepository.findByDonHang_MaDonHang(maDonHang);
    }

    private void validateGioHang(List<BanHangItemDTO> gioHang) {
        if (gioHang == null || gioHang.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng không được để trống.");
        }
        for (BanHangItemDTO item : gioHang) {
            if (item == null || !StringUtils.hasText(item.getMaMon())) {
                throw new IllegalArgumentException("Có món không hợp lệ trong giỏ hàng.");
            }
            if (item.getSoLuong() == null || item.getSoLuong() <= 0) {
                throw new IllegalArgumentException("Số lượng món phải lớn hơn 0.");
            }
            if (item.getDonGia() == null || item.getDonGia().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Đơn giá món không hợp lệ.");
            }
        }
    }

    private void validateRequest(BanHangRequestDTO requestDTO) {
        if (requestDTO == null || !StringUtils.hasText(requestDTO.getLoaiDonHang())) {
            throw new IllegalArgumentException("Vui lòng chọn loại đơn hàng.");
        }
        if (!"Dùng tại quán".equalsIgnoreCase(requestDTO.getLoaiDonHang())
                && !"Mang đi".equalsIgnoreCase(requestDTO.getLoaiDonHang())) {
            throw new IllegalArgumentException("Loại đơn hàng không hợp lệ.");
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
            throw new IllegalArgumentException("Trạng thái đơn hàng không được để trống.");
        }
        String normalized = normalize(trangThai);
        if (!List.of("Đang phục vụ", "Chờ thanh toán", "Đang xử lý", "Đã thanh toán", "Đã hủy").contains(normalized)) {
            throw new IllegalArgumentException("Trạng thái đơn hàng không hợp lệ.");
        }
        return normalized;
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
