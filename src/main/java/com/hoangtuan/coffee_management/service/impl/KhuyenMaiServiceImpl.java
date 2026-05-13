package com.hoangtuan.coffee_management.service.impl;

import com.hoangtuan.coffee_management.entity.KhuyenMai;
import com.hoangtuan.coffee_management.repository.KhuyenMaiRepository;
import com.hoangtuan.coffee_management.service.KhuyenMaiService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class KhuyenMaiServiceImpl implements KhuyenMaiService {

    private final KhuyenMaiRepository khuyenMaiRepository;

    public KhuyenMaiServiceImpl(KhuyenMaiRepository khuyenMaiRepository) {
        this.khuyenMaiRepository = khuyenMaiRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<KhuyenMai> findKhuyenMaiConHieuLuc() {
        LocalDateTime now = LocalDateTime.now();
        return khuyenMaiRepository.findByTrangThai(Boolean.TRUE).stream()
                .filter(khuyenMai -> khuyenMai.getNgayBatDau() == null || !khuyenMai.getNgayBatDau().isAfter(now))
                .filter(khuyenMai -> khuyenMai.getNgayKetThuc() == null || !khuyenMai.getNgayKetThuc().isBefore(now))
                .sorted((left, right) -> safeString(left.getMaKhuyenMai()).compareToIgnoreCase(safeString(right.getMaKhuyenMai())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public KhuyenMai findById(String maKhuyenMai) {
        if (!StringUtils.hasText(maKhuyenMai)) {
            throw new IllegalArgumentException("Vui long chon khuyen mai.");
        }
        return khuyenMaiRepository.findById(maKhuyenMai.trim())
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay khuyen mai."));
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }
}
