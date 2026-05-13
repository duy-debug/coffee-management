package com.hoangtuan.coffee_management.dto;

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
public class PhieuXuatKhoDetailDTO {

    private String maPhieuXuat;
    private LocalDateTime ngayXuat;
    private String lyDoXuat;
    private String tenNhanVien;
    private List<ChiTietPhieuXuatViewDTO> danhSachChiTiet;
}
