package com.hoangtuan.coffee_management.service;

import com.hoangtuan.coffee_management.dto.PhieuNhapKhoDetailDTO;
import com.hoangtuan.coffee_management.dto.PhieuNhapKhoFormDTO;
import com.hoangtuan.coffee_management.dto.PhieuNhapKhoListDTO;
import com.hoangtuan.coffee_management.entity.PhieuNhapKho;
import java.util.List;

public interface PhieuNhapKhoService {

    List<PhieuNhapKhoListDTO> findAll();

    List<PhieuNhapKhoListDTO> search(String keyword);

    PhieuNhapKho findById(String maPhieuNhap);

    PhieuNhapKhoDetailDTO getChiTiet(String maPhieuNhap);

    void createPhieuNhap(PhieuNhapKhoFormDTO dto, String tenDangNhap);

    String generateNextMaPhieuNhap();

    String generateNextMaCTPN();

    java.math.BigDecimal tinhTongTien(String maPhieuNhap);
}
