package com.hoangtuan.coffee_management.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhieuNhapKhoDetailDTO {

    private String maPhieuNhap;
    private LocalDateTime ngayNhap;
    private String ghiChu;
    private String tenNhanVien;
    private List<ChiTietPhieuNhapViewDTO> danhSachChiTiet;
    private BigDecimal tongTien;
}
