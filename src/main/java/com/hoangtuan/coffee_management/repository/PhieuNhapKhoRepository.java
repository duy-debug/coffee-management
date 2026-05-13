package com.hoangtuan.coffee_management.repository;

import com.hoangtuan.coffee_management.entity.PhieuNhapKho;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhieuNhapKhoRepository extends JpaRepository<PhieuNhapKho, String> {

    List<PhieuNhapKho> findByNgayNhapBetween(LocalDateTime tuNgay, LocalDateTime denNgay);
}

