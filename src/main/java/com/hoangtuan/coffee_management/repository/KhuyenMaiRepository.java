package com.hoangtuan.coffee_management.repository;

import com.hoangtuan.coffee_management.entity.KhuyenMai;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KhuyenMaiRepository extends JpaRepository<KhuyenMai, String> {

    List<KhuyenMai> findByTrangThai(Boolean trangThai);

    List<KhuyenMai> findByTenKhuyenMaiContainingIgnoreCase(String tenKhuyenMai);
}

