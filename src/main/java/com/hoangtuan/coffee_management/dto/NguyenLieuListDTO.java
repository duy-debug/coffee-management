package com.hoangtuan.coffee_management.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NguyenLieuListDTO {

    private String maNguyenLieu;
    private String tenNguyenLieu;
    private String donViTinh;
    private Integer soLuongTon;
    private Integer mucTonToiThieu;
    private boolean sapHet;
    private String trangThaiHienThi;
    private String badgeClass;
}
