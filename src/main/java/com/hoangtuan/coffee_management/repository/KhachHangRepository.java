package com.hoangtuan.coffee_management.repository;

import com.hoangtuan.coffee_management.entity.KhachHang;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KhachHangRepository extends JpaRepository<KhachHang, String> {

    List<KhachHang> findByHoTenContainingIgnoreCase(String hoTen);

    Optional<KhachHang> findBySoDienThoai(String soDienThoai);
}

