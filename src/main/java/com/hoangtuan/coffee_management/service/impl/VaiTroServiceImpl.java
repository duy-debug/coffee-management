package com.hoangtuan.coffee_management.service.impl;

import com.hoangtuan.coffee_management.entity.VaiTro;
import com.hoangtuan.coffee_management.repository.VaiTroRepository;
import com.hoangtuan.coffee_management.service.VaiTroService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VaiTroServiceImpl implements VaiTroService {

    private final VaiTroRepository vaiTroRepository;

    public VaiTroServiceImpl(VaiTroRepository vaiTroRepository) {
        this.vaiTroRepository = vaiTroRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VaiTro> getVaiTroCoDinh() {
        return vaiTroRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public VaiTro getByTenVaiTro(String tenVaiTro) {
        return vaiTroRepository.findByTenVaiTro(tenVaiTro)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vai trò: " + tenVaiTro));
    }
}
