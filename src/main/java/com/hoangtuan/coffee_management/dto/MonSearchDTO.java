package com.hoangtuan.coffee_management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonSearchDTO {

    private String keyword;
    private String maNhomMon;
    private String trangThai;
}
