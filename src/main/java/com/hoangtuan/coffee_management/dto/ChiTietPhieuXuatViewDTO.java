package com.hoangtuan.coffee_management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietPhieuXuatViewDTO {

    private String maCTPX;
    private String maNguyenLieu;
    private String tenNguyenLieu;
    private Integer soLuongXuat;
    private String donViTinh;
}
