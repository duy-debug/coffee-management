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
public class KhuyenMaiListDTO {

    private String maKhuyenMai;
    private String tenKhuyenMai;
    private String loaiKhuyenMai;
    private BigDecimal giaTriGiam;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private Boolean trangThai;
    private boolean conHieuLuc;
    private String trangThaiHienThi;
    private String badgeClass;
}
