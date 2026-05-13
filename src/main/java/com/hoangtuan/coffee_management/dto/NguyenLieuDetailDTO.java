package com.hoangtuan.coffee_management.dto;

import com.hoangtuan.coffee_management.entity.NguyenLieu;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NguyenLieuDetailDTO {

    private NguyenLieu nguyenLieu;
    private boolean sapHet;
    private long soPhieuNhap;
    private long tongSoLuongNhap;
    private long soPhieuXuat;
    private long tongSoLuongXuat;
    private List<String> nhapGanNhat;
    private List<String> xuatGanNhat;
}
