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
public class BaoCaoTongQuanDTO {

    private BigDecimal doanhThuHomNay;
    private BigDecimal doanhThuThangNay;
    private long soDonHangHomNay;
    private long soHoaDonDaThanhToan;
    private long soMonDangBan;
    private long soNguyenLieuSapHet;
    private long soNhanVienDangLam;
    private long tongNguyenLieu;
}
