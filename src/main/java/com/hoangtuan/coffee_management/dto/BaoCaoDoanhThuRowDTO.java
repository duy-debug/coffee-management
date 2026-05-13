package com.hoangtuan.coffee_management.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaoCaoDoanhThuRowDTO {

    private String maHoaDon;
    private LocalDateTime ngayThanhToan;
    private String maDonHang;
    private String phuongThucThanhToan;
    private BigDecimal tongTien;
    private BigDecimal soTienGiam;
    private BigDecimal soTienPhaiTra;
}
