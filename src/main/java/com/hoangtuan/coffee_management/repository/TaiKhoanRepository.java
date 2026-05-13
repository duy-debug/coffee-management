package com.hoangtuan.coffee_management.repository;

import com.hoangtuan.coffee_management.entity.TaiKhoan;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, String> {

    Optional<TaiKhoan> findByTenDangNhap(String tenDangNhap);

    boolean existsByTenDangNhap(String tenDangNhap);

    Optional<TaiKhoan> findByNhanVien_MaNhanVien(String maNhanVien);
}

