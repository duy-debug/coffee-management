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
public class ChiTietPhieuNhapFormDTO {

    private String maNguyenLieu;
    private Integer soLuongNhap;
    private BigDecimal donGiaNhap;
}
