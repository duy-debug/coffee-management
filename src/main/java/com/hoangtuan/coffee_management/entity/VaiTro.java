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
@Table(name = "vai_tro")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VaiTro {

    @Id
    @Column(name = "ma_vai_tro", length = 10, nullable = false)
    private String maVaiTro;

    @Column(name = "ten_vai_tro", length = 50, nullable = false)
    private String tenVaiTro;

    @Column(name = "mo_ta", length = 255)
    private String moTa;
}

