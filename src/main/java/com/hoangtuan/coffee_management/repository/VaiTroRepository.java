package com.hoangtuan.coffee_management.repository;

import com.hoangtuan.coffee_management.entity.VaiTro;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VaiTroRepository extends JpaRepository<VaiTro, String> {

    Optional<VaiTro> findByTenVaiTro(String tenVaiTro);
}

