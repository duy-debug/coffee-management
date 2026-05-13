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
@Table(name = "ban")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ban {

    @Id
    @Column(name = "ma_ban", length = 10, nullable = false)
    private String maBan;

    @Column(name = "ten_ban", length = 50, nullable = false)
    private String tenBan;

    @Column(name = "khu_vuc", length = 50)
    private String khuVuc;

    @Column(name = "trang_thai", length = 50, nullable = false)
    private String trangThai;

    @Column(name = "ghi_chu", length = 255)
    private String ghiChu;
}

