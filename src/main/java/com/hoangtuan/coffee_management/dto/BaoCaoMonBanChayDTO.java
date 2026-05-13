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
public class BaoCaoMonBanChayDTO {

    private LocalDate tuNgay;
    private LocalDate denNgay;
    private List<BaoCaoMonBanChayRowDTO> danhSach;
    private long tongSoLuongBan;
    private BigDecimal tongDoanhThuMon;
}
