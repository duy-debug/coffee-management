package com.hoangtuan.coffee_management.repository;

import com.hoangtuan.coffee_management.entity.TaiKhoan;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, String> {

    Optional<TaiKhoan> findByTenDangNhap(String tenDangNhap);

    boolean existsByTenDangNhap(String tenDangNhap);

    boolean existsByTenDangNhapAndMaTaiKhoanNot(String tenDangNhap, String maTaiKhoan);

    Optional<TaiKhoan> findByNhanVien_MaNhanVien(String maNhanVien);

    List<TaiKhoan> findByMaTaiKhoanContainingIgnoreCaseOrTenDangNhapContainingIgnoreCaseOrNhanVien_HoTenContainingIgnoreCase(
            String maTaiKhoan,
            String tenDangNhap,
            String hoTen
    );

    List<TaiKhoan> findByTrangThai(Boolean trangThai);

    List<TaiKhoan> findByVaiTro_TenVaiTro(String tenVaiTro);

    long countByTrangThaiTrueAndVaiTro_TenVaiTro(String tenVaiTro);
}

