package com.hoangtuan.coffee_management.dto;

import com.hoangtuan.coffee_management.entity.KhachHang;
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
public class KhachHangDetailDTO {

    private KhachHang khachHang;
    private long tongSoDonHang;
    private long tongSoHoaDon;
    private BigDecimal tongTienDaMua;
    private List<LichSuMuaHangDTO> lichSuMuaHangs;
}
