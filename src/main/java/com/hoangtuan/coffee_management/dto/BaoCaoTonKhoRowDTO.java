package com.hoangtuan.coffee_management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaoCaoTonKhoRowDTO {

    private String maNguyenLieu;
    private String tenNguyenLieu;
    private String donViTinh;
    private Integer soLuongTon;
    private Integer mucTonToiThieu;
    private String trangThai;
}
