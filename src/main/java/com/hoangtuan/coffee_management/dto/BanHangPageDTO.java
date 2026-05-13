package com.hoangtuan.coffee_management.dto;

import com.hoangtuan.coffee_management.entity.Ban;
import com.hoangtuan.coffee_management.entity.KhachHang;
import com.hoangtuan.coffee_management.entity.Mon;
import com.hoangtuan.coffee_management.entity.NhomMon;
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
public class BanHangPageDTO {

    private String keyword;
    private String maNhomMon;
    private String loaiDonHang;
    private String maBan;
    private String maKhachHang;
    private List<Ban> danhSachBanTrong;
    private List<NhomMon> danhSachNhomMon;
    private List<Mon> danhSachMon;
    private List<KhachHang> danhSachKhachHang;
    private List<BanHangItemDTO> gioHang;
    private BigDecimal tongTien;
}
