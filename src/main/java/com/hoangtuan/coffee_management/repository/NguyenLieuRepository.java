package com.hoangtuan.coffee_management.repository;

import com.hoangtuan.coffee_management.entity.NguyenLieu;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NguyenLieuRepository extends JpaRepository<NguyenLieu, String> {

    List<NguyenLieu> findByMaNguyenLieuContainingIgnoreCaseOrTenNguyenLieuContainingIgnoreCase(String maNguyenLieu, String tenNguyenLieu);

    @Query("select n from NguyenLieu n where n.soLuongTon <= n.mucTonToiThieu")
    List<NguyenLieu> findNguyenLieuSapHet();
}

