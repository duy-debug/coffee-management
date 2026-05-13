package com.hoangtuan.coffee_management.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaoCaoTonKhoDTO {

    private List<BaoCaoTonKhoRowDTO> danhSach;
    private long tongNguyenLieu;
    private long tongNguyenLieuSapHet;
}
