package com.hoangtuan.coffee_management.repository;

import com.hoangtuan.coffee_management.entity.ChiTietDonHang;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChiTietDonHangRepository extends JpaRepository<ChiTietDonHang, String> {

    List<ChiTietDonHang> findByDonHang_MaDonHang(String maDonHang);
}

