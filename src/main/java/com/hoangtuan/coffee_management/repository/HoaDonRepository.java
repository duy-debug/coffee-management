package com.hoangtuan.coffee_management.repository;

import com.hoangtuan.coffee_management.entity.HoaDon;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HoaDonRepository extends JpaRepository<HoaDon, String> {

    Optional<HoaDon> findByDonHang_MaDonHang(String maDonHang);

    List<HoaDon> findByNgayThanhToanBetween(LocalDateTime tuNgay, LocalDateTime denNgay);

    long countByKhuyenMai_MaKhuyenMai(String maKhuyenMai);

    List<HoaDon> findByKhuyenMai_MaKhuyenMai(String maKhuyenMai);

    List<HoaDon> findByKhuyenMai_MaKhuyenMaiAndSoTienGiamGreaterThan(String maKhuyenMai, BigDecimal soTienGiam);
}

