package com.hoangtuan.coffee_management.service;

import com.hoangtuan.coffee_management.dto.KhuyenMaiDetailDTO;
import com.hoangtuan.coffee_management.dto.KhuyenMaiFormDTO;
import com.hoangtuan.coffee_management.dto.KhuyenMaiListDTO;
import com.hoangtuan.coffee_management.entity.KhuyenMai;
import java.util.List;

public interface KhuyenMaiService {

    List<KhuyenMaiListDTO> getDanhSach(String keyword, String loaiKhuyenMai, Boolean trangThai);

    List<KhuyenMai> findAll();

    List<KhuyenMai> searchAndFilter(String keyword, String loaiKhuyenMai, Boolean trangThai);

    List<KhuyenMai> findKhuyenMaiConHieuLuc();

    KhuyenMai findById(String maKhuyenMai);

    KhuyenMaiFormDTO getFormThem();

    KhuyenMaiFormDTO getFormSua(String maKhuyenMai);

    void save(KhuyenMaiFormDTO dto);

    void update(String maKhuyenMai, KhuyenMaiFormDTO dto);

    void ngungApDung(String maKhuyenMai);

    void kichHoat(String maKhuyenMai);

    KhuyenMaiDetailDTO getChiTiet(String maKhuyenMai);

    boolean kiemTraConHieuLuc(KhuyenMai khuyenMai);

    String generateNextMaKhuyenMai();
}
