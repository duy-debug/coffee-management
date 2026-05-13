package com.hoangtuan.coffee_management.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhieuXuatKhoFormDTO {

    private String maPhieuXuat;
    private String lyDoXuat;
    private List<ChiTietPhieuXuatFormDTO> danhSachChiTiet = new ArrayList<>();
}
