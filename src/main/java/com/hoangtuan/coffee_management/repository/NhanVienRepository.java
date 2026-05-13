package com.hoangtuan.coffee_management.repository;

import com.hoangtuan.coffee_management.entity.NhanVien;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NhanVienRepository extends JpaRepository<NhanVien, String> {

    List<NhanVien> findByHoTenContainingIgnoreCase(String hoTen);

    Optional<NhanVien> findBySoDienThoai(String soDienThoai);
}

