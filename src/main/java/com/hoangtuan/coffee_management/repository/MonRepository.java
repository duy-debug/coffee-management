package com.hoangtuan.coffee_management.repository;

import com.hoangtuan.coffee_management.entity.Mon;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonRepository extends JpaRepository<Mon, String> {

    List<Mon> findByTrangThai(Boolean trangThai);

    List<Mon> findByNhomMon_MaNhomMon(String maNhomMon);
}

