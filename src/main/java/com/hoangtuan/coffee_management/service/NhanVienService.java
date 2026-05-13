package com.hoangtuan.coffee_management.service;

import com.hoangtuan.coffee_management.dto.NhanVienFormDTO;
import com.hoangtuan.coffee_management.entity.NhanVien;
import java.util.List;

public interface NhanVienService {

    List<NhanVien> getDanhSach(String keyword, String trangThai);

    List<NhanVien> getNhanVienChuaCoTaiKhoan();

    NhanVienFormDTO getFormThem();

    NhanVienFormDTO getFormSua(String maNhanVien);

    void taoNhanVien(NhanVienFormDTO dto);

    void capNhatNhanVien(String maNhanVien, NhanVienFormDTO dto);

    void ngungNhanVien(String maNhanVien);

    void kichHoatNhanVien(String maNhanVien);
}
