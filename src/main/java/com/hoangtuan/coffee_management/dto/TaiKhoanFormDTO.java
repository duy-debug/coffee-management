package com.hoangtuan.coffee_management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaiKhoanFormDTO {

    private String maTaiKhoan;
    private String tenDangNhap;
    private String matKhau;
    private Boolean trangThai;
    private String tenVaiTro;
    private String maNhanVien;
    private String tenNhanVien;
}
