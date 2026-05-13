package com.hoangtuan.coffee_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "nguyen_lieu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NguyenLieu {

    @Id
    @Column(name = "ma_nguyen_lieu", length = 10, nullable = false)
    private String maNguyenLieu;

    @Column(name = "ten_nguyen_lieu", length = 100, nullable = false)
    private String tenNguyenLieu;

    @Column(name = "don_vi_tinh", length = 20, nullable = false)
    private String donViTinh;

    @Column(name = "so_luong_ton", nullable = false)
    private Integer soLuongTon;

    @Column(name = "muc_ton_toi_thieu", nullable = false)
    private Integer mucTonToiThieu;
}

