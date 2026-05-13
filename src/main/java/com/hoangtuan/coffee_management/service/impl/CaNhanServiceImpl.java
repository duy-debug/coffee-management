package com.hoangtuan.coffee_management.service.impl;

import com.hoangtuan.coffee_management.dto.CapNhatCaNhanDTO;
import com.hoangtuan.coffee_management.dto.DoiMatKhauDTO;
import com.hoangtuan.coffee_management.dto.ThongTinCaNhanDTO;
import com.hoangtuan.coffee_management.entity.NhanVien;
import com.hoangtuan.coffee_management.entity.TaiKhoan;
import com.hoangtuan.coffee_management.repository.NhanVienRepository;
import com.hoangtuan.coffee_management.repository.TaiKhoanRepository;
import com.hoangtuan.coffee_management.service.CaNhanService;
import java.util.Objects;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class CaNhanServiceImpl implements CaNhanService {

    private final TaiKhoanRepository taiKhoanRepository;
    private final NhanVienRepository nhanVienRepository;
    private final PasswordEncoder passwordEncoder;

    public CaNhanServiceImpl(
            TaiKhoanRepository taiKhoanRepository,
            NhanVienRepository nhanVienRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public ThongTinCaNhanDTO getCurrentUserProfile(String tenDangNhap) {
        TaiKhoan taiKhoan = findTaiKhoanByTenDangNhap(tenDangNhap);
        NhanVien nhanVien = requireNhanVien(taiKhoan);
        return mapToThongTinCaNhanDTO(taiKhoan, nhanVien);
    }

    @Override
    @Transactional
    public void updateCurrentUserProfile(String tenDangNhap, CapNhatCaNhanDTO dto) {
        validateCapNhatCaNhan(dto);

        TaiKhoan taiKhoan = findTaiKhoanByTenDangNhap(tenDangNhap);
        NhanVien nhanVien = requireNhanVien(taiKhoan);

        nhanVien.setHoTen(normalize(dto.getHoTen()));
        nhanVien.setSoDienThoai(normalize(dto.getSoDienThoai()));
        nhanVien.setDiaChi(normalize(dto.getDiaChi()));

        nhanVienRepository.save(nhanVien);
    }

    @Override
    @Transactional
    public void changePassword(String tenDangNhap, DoiMatKhauDTO dto) {
        validateDoiMatKhau(dto);

        TaiKhoan taiKhoan = findTaiKhoanByTenDangNhap(tenDangNhap);

        if (!passwordEncoder.matches(dto.getMatKhauHienTai(), taiKhoan.getMatKhau())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không đúng.");
        }

        if (!Objects.equals(dto.getMatKhauMoi(), dto.getXacNhanMatKhauMoi())) {
            throw new IllegalArgumentException("Mật khẩu mới và xác nhận mật khẩu mới không khớp.");
        }

        taiKhoan.setMatKhau(passwordEncoder.encode(dto.getMatKhauMoi()));
        taiKhoanRepository.save(taiKhoan);
    }

    private TaiKhoan findTaiKhoanByTenDangNhap(String tenDangNhap) {
        return taiKhoanRepository.findByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản đang đăng nhập."));
    }

    private NhanVien requireNhanVien(TaiKhoan taiKhoan) {
        NhanVien nhanVien = taiKhoan.getNhanVien();
        if (nhanVien == null) {
            throw new IllegalArgumentException("Tài khoản chưa được gắn với thông tin nhân viên.");
        }
        return nhanVien;
    }

    private ThongTinCaNhanDTO mapToThongTinCaNhanDTO(TaiKhoan taiKhoan, NhanVien nhanVien) {
        return new ThongTinCaNhanDTO(
                taiKhoan.getTenDangNhap(),
                taiKhoan.getVaiTro() != null ? taiKhoan.getVaiTro().getTenVaiTro() : "",
                nhanVien.getMaNhanVien(),
                nhanVien.getHoTen(),
                nhanVien.getSoDienThoai(),
                nhanVien.getDiaChi(),
                nhanVien.getChucVu(),
                Boolean.TRUE.equals(taiKhoan.getTrangThai()) ? "Hoạt động" : "Bị khóa",
                Boolean.TRUE.equals(nhanVien.getTrangThai()) ? "Đang làm việc" : "Ngừng làm việc"
        );
    }

    private void validateCapNhatCaNhan(CapNhatCaNhanDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Dữ liệu cập nhật không hợp lệ.");
        }
        if (!StringUtils.hasText(dto.getHoTen())) {
            throw new IllegalArgumentException("Họ tên không được để trống.");
        }
        if (!StringUtils.hasText(dto.getSoDienThoai())) {
            throw new IllegalArgumentException("Số điện thoại không được để trống.");
        }
        if (dto.getSoDienThoai().trim().length() > 15) {
            throw new IllegalArgumentException("Số điện thoại không được vượt quá 15 ký tự.");
        }
    }

    private void validateDoiMatKhau(DoiMatKhauDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Dữ liệu đổi mật khẩu không hợp lệ.");
        }
        if (!StringUtils.hasText(dto.getMatKhauHienTai())) {
            throw new IllegalArgumentException("Vui lòng nhập mật khẩu hiện tại.");
        }
        if (!StringUtils.hasText(dto.getMatKhauMoi())) {
            throw new IllegalArgumentException("Vui lòng nhập mật khẩu mới.");
        }
        if (!StringUtils.hasText(dto.getXacNhanMatKhauMoi())) {
            throw new IllegalArgumentException("Vui lòng xác nhận mật khẩu mới.");
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
