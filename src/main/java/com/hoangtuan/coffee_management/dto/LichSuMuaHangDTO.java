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
public class LichSuMuaHangDTO {

    private String maDonHang;
    private LocalDateTime ngayDat;
    private String loaiDonHang;
    private String trangThaiDonHang;
    private BigDecimal tongTienDonHang;
    private String maHoaDon;
    private BigDecimal soTienPhaiTra;
    private LocalDateTime ngayThanhToan;
    private String phuongThucThanhToan;
}
