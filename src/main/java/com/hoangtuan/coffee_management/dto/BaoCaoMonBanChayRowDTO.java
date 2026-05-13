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
public class BaoCaoMonBanChayRowDTO {

    private String maMon;
    private String tenMon;
    private String tenNhomMon;
    private long tongSoLuongBan;
    private BigDecimal tongDoanhThuMon;
}
