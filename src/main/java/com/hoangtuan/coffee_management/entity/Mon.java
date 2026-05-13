package com.hoangtuan.coffee_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "mon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mon {

    @Id
    @Column(name = "ma_mon", length = 10, nullable = false)
    private String maMon;

    @Column(name = "ten_mon", length = 100, nullable = false)
    private String tenMon;

    @Column(name = "don_gia", nullable = false, precision = 18, scale = 2)
    private BigDecimal donGia;

    @Column(name = "mo_ta", length = 255)
    private String moTa;

    @Column(name = "hinh_anh", length = 255)
    private String hinhAnh;

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_nhom_mon", referencedColumnName = "ma_nhom_mon", nullable = false)
    private NhomMon nhomMon;
}

