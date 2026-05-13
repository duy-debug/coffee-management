package com.hoangtuan.coffee_management.repository;

import com.hoangtuan.coffee_management.entity.NguyenLieu;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NguyenLieuRepository extends JpaRepository<NguyenLieu, String> {

    List<NguyenLieu> findBySoLuongTonLessThanEqual(Integer mucTonToiThieu);
}

