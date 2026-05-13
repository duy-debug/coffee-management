package com.hoangtuan.coffee_management.dto;

import com.hoangtuan.coffee_management.entity.ChiTietDonHang;
import com.hoangtuan.coffee_management.entity.DonHang;
import com.hoangtuan.coffee_management.entity.KhuyenMai;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HoaDonThanhToanViewDTO {

    private DonHang donHang;
    private List<ChiTietDonHang> chiTietDonHangs;
    private List<KhuyenMai> danhSachKhuyenMai;
    private BigDecimal tongTien;
    private BigDecimal soTienGiam;
    private BigDecimal soTienPhaiTra;
    private String phuongThucThanhToan;
    private String maKhuyenMai;
}
