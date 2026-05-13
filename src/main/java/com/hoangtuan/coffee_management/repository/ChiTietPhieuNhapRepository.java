package com.hoangtuan.coffee_management.repository;

import com.hoangtuan.coffee_management.entity.ChiTietPhieuNhap;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChiTietPhieuNhapRepository extends JpaRepository<ChiTietPhieuNhap, String> {

    List<ChiTietPhieuNhap> findByPhieuNhapKho_MaPhieuNhap(String maPhieuNhap);
}

