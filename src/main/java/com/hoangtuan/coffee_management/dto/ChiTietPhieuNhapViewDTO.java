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
public class ChiTietPhieuNhapViewDTO {

    private String maCTPN;
    private String maNguyenLieu;
    private String tenNguyenLieu;
    private Integer soLuongNhap;
    private BigDecimal donGiaNhap;
    private BigDecimal thanhTien;
}
