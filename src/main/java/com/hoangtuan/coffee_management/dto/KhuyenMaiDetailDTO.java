package com.hoangtuan.coffee_management.dto;

import com.hoangtuan.coffee_management.entity.KhuyenMai;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhuyenMaiDetailDTO {

    private KhuyenMai khuyenMai;
    private long soHoaDonDaApDung;
    private BigDecimal tongTienDaGiam;
    private boolean conHieuLuc;
}
