package com.hoangtuan.coffee_management.repository;

import com.hoangtuan.coffee_management.entity.NhomMon;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NhomMonRepository extends JpaRepository<NhomMon, String> {

    List<NhomMon> findByTenNhomMonContainingIgnoreCase(String tenNhomMon);
}

