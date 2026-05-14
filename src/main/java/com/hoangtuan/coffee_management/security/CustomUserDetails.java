package com.hoangtuan.coffee_management.security;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

    private final String tenDangNhap;
    private final String matKhau;
    private final boolean trangThai;
    private final List<GrantedAuthority> authorities;
    private final String maNhanVien;
    private final String hoTenNhanVien;

    public CustomUserDetails(
            String tenDangNhap,
            String matKhau,
            boolean trangThai,
            List<GrantedAuthority> authorities,
            String maNhanVien,
            String hoTenNhanVien
    ) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.trangThai = trangThai;
        this.authorities = authorities;
        this.maNhanVien = maNhanVien;
        this.hoTenNhanVien = hoTenNhanVien;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public String getHoTenNhanVien() {
        return hoTenNhanVien;
    }

    public String getDisplayName() {
        if (hoTenNhanVien != null && !hoTenNhanVien.isBlank()) {
            return hoTenNhanVien;
        }
        return tenDangNhap;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return matKhau;
    }

    @Override
    public String getUsername() {
        return tenDangNhap;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return trangThai;
    }
}
