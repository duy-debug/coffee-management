package com.hoangtuan.coffee_management.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaoCaoDoanhThuDTO {

    private LocalDate tuNgay;
    private LocalDate denNgay;
    private List<BaoCaoDoanhThuRowDTO> danhSach;
    private long tongSoHoaDon;
    private BigDecimal tongDoanhThuTruocGiam;
    private BigDecimal tongTienGiam;
    private BigDecimal tongDoanhThuThucThu;
}
