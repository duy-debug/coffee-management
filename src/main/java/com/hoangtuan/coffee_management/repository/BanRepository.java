package com.hoangtuan.coffee_management.repository;

import com.hoangtuan.coffee_management.entity.Ban;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BanRepository extends JpaRepository<Ban, String> {

    List<Ban> findByTrangThai(String trangThai);
}

