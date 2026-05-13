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
public class MonFormDTO {

    private String maMon;
    private String tenMon;
    private BigDecimal donGia;
    private String moTa;
    private String hinhAnh;
    private Boolean trangThai;
    private String maNhomMon;
    private String tenNhomMon;
}
