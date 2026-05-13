package com.hoangtuan.coffee_management.repository;

import com.hoangtuan.coffee_management.entity.PhieuXuatKho;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhieuXuatKhoRepository extends JpaRepository<PhieuXuatKho, String> {

    List<PhieuXuatKho> findByNgayXuatBetween(LocalDateTime tuNgay, LocalDateTime denNgay);
}

