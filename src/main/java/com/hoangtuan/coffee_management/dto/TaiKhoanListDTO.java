package com.hoangtuan.coffee_management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaiKhoanListDTO {

    private String maTaiKhoan;
    private String tenDangNhap;
    private String trangThai;
    private String tenVaiTro;
    private String maNhanVien;
    private String hoTenNhanVien;
    private String chucVuNhanVien;
}
