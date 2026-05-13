package com.hoangtuan.coffee_management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhachHangFormDTO {

    private String maKhachHang;
    private String hoTen;
    private String soDienThoai;
    private String ghiChu;
}
