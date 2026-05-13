package com.hoangtuan.coffee_management.repository;

import com.hoangtuan.coffee_management.entity.DonHang;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonHangRepository extends JpaRepository<DonHang, String> {

    List<DonHang> findByTrangThai(String trangThai);

    List<DonHang> findByNgayDatBetween(LocalDateTime tuNgay, LocalDateTime denNgay);
}

