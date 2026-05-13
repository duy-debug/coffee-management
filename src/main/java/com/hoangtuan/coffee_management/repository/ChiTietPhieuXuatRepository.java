package com.hoangtuan.coffee_management.repository;

import com.hoangtuan.coffee_management.entity.ChiTietPhieuXuat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChiTietPhieuXuatRepository extends JpaRepository<ChiTietPhieuXuat, String> {

    List<ChiTietPhieuXuat> findByPhieuXuatKho_MaPhieuXuat(String maPhieuXuat);
}

