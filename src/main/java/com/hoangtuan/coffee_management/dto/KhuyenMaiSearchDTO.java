package com.hoangtuan.coffee_management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhuyenMaiSearchDTO {

    private String keyword;
    private String loaiKhuyenMai;
    private String trangThai;
}
