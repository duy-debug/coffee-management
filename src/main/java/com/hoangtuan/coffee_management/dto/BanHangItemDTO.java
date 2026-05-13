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
public class BanHangItemDTO {

    private String maMon;
    private String tenMon;
    private String hinhAnh;
    private String maNhomMon;
    private String tenNhomMon;
    private BigDecimal donGia;
    private Integer soLuong;
    private BigDecimal thanhTien;
}
