package com.hoangtuan.coffee_management.service;

import com.hoangtuan.coffee_management.dto.PhieuXuatKhoDetailDTO;
import com.hoangtuan.coffee_management.dto.PhieuXuatKhoFormDTO;
import com.hoangtuan.coffee_management.dto.PhieuXuatKhoListDTO;
import com.hoangtuan.coffee_management.entity.PhieuXuatKho;
import java.util.List;

public interface PhieuXuatKhoService {

    List<PhieuXuatKhoListDTO> findAll();

    List<PhieuXuatKhoListDTO> search(String keyword);

    PhieuXuatKho findById(String maPhieuXuat);

    PhieuXuatKhoDetailDTO getChiTiet(String maPhieuXuat);

    void createPhieuXuat(PhieuXuatKhoFormDTO dto, String tenDangNhap);

    String generateNextMaPhieuXuat();

    String generateNextMaCTPX();
}
