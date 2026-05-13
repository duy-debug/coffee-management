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
public class PhieuNhapKhoListDTO {

    private String maPhieuNhap;
    private LocalDateTime ngayNhap;
    private String tenNhanVien;
    private String ghiChu;
    private long tongSoNguyenLieu;
    private BigDecimal tongTien;
}
