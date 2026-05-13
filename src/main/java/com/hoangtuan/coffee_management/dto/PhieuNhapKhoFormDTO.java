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
public class PhieuNhapKhoFormDTO {

    private String maPhieuNhap;
    private String ghiChu;
    private List<ChiTietPhieuNhapFormDTO> danhSachChiTiet = new ArrayList<>();
}
