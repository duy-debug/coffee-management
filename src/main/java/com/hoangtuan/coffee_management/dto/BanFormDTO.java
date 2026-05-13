package com.hoangtuan.coffee_management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BanFormDTO {

    private String maBan;
    private String tenBan;
    private String khuVuc;
    private String trangThai;
    private String ghiChu;
}
