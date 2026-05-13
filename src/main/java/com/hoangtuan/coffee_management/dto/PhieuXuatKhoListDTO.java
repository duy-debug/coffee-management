package com.hoangtuan.coffee_management.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhieuXuatKhoListDTO {

    private String maPhieuXuat;
    private LocalDateTime ngayXuat;
    private String tenNhanVien;
    private String lyDoXuat;
    private long tongSoNguyenLieu;
}
