package com.hoangtuan.coffee_management.security;

import com.hoangtuan.coffee_management.entity.TaiKhoan;
import com.hoangtuan.coffee_management.repository.TaiKhoanRepository;
import java.util.List;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final TaiKhoanRepository taiKhoanRepository;

    public CustomUserDetailsService(TaiKhoanRepository taiKhoanRepository) {
        this.taiKhoanRepository = taiKhoanRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String tenDangNhap) throws UsernameNotFoundException {
        TaiKhoan taiKhoan = taiKhoanRepository.findByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản: " + tenDangNhap));

        if (Boolean.FALSE.equals(taiKhoan.getTrangThai())) {
            throw new DisabledException("Tài khoản đã bị khóa");
        }

        String tenVaiTro = taiKhoan.getVaiTro().getTenVaiTro();
        String maNhanVien = taiKhoan.getNhanVien() != null ? taiKhoan.getNhanVien().getMaNhanVien() : null;
        String hoTenNhanVien = taiKhoan.getNhanVien() != null ? taiKhoan.getNhanVien().getHoTen() : null;
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(tenVaiTro));

        return new CustomUserDetails(
                taiKhoan.getTenDangNhap(),
                taiKhoan.getMatKhau(),
                Boolean.TRUE.equals(taiKhoan.getTrangThai()),
                authorities,
                maNhanVien,
                hoTenNhanVien
        );
    }
}
