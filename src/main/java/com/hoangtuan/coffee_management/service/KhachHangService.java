package com.hoangtuan.coffee_management.service;

import com.hoangtuan.coffee_management.dto.KhachHangDetailDTO;
import com.hoangtuan.coffee_management.dto.KhachHangFormDTO;
import com.hoangtuan.coffee_management.dto.LichSuMuaHangDTO;
import com.hoangtuan.coffee_management.entity.KhachHang;
import java.util.List;

public interface KhachHangService {

    List<KhachHang> findAll();

    List<KhachHang> search(String keyword);

    KhachHang findById(String maKhachHang);

    KhachHangFormDTO getFormThem();

    KhachHangFormDTO getFormSua(String maKhachHang);

    void save(KhachHangFormDTO dto);

    void update(String maKhachHang, KhachHangFormDTO dto);

    KhachHangDetailDTO getChiTiet(String maKhachHang);

    List<LichSuMuaHangDTO> getLichSuMuaHang(String maKhachHang);

    String generateNextMaKhachHang();
}
