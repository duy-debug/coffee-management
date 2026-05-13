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
@Table(name = "nhom_mon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NhomMon {

    @Id
    @Column(name = "ma_nhom_mon", length = 10, nullable = false)
    private String maNhomMon;

    @Column(name = "ten_nhom_mon", length = 50, nullable = false)
    private String tenNhomMon;

    @Column(name = "mo_ta", length = 255)
    private String moTa;
}

