package com.hoangtuan.coffee_management.service;

import com.hoangtuan.coffee_management.dto.ResetMatKhauDTO;
import com.hoangtuan.coffee_management.dto.TaiKhoanFormDTO;
import com.hoangtuan.coffee_management.dto.TaiKhoanListDTO;
import com.hoangtuan.coffee_management.entity.NhanVien;
import java.util.List;

public interface TaiKhoanService {

    List<TaiKhoanListDTO> getDanhSach(String keyword, String trangThai, String vaiTro);

    List<NhanVien> getNhanVienChuaCoTaiKhoanOptions();

    TaiKhoanFormDTO getFormThem();

    TaiKhoanFormDTO getFormSua(String maTaiKhoan);

    ResetMatKhauDTO getFormResetMatKhau();

    void taoTaiKhoan(TaiKhoanFormDTO dto);

    void capNhatTaiKhoan(String maTaiKhoan, TaiKhoanFormDTO dto);

    void khoaTaiKhoan(String maTaiKhoan, String currentUsername);

    void moKhoaTaiKhoan(String maTaiKhoan);

    void resetMatKhau(String maTaiKhoan, ResetMatKhauDTO dto);
}
