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
@Table(name = "chi_tiet_phieu_nhap")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietPhieuNhap {

    @Id
    @Column(name = "ma_ctpn", length = 10, nullable = false)
    private String maCTPN;

    @Column(name = "so_luong_nhap", nullable = false)
    private Integer soLuongNhap;

    @Column(name = "don_gia_nhap", nullable = false, precision = 18, scale = 2)
    private BigDecimal donGiaNhap;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_phieu_nhap", referencedColumnName = "ma_phieu_nhap", nullable = false)
    private PhieuNhapKho phieuNhapKho;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_nguyen_lieu", referencedColumnName = "ma_nguyen_lieu", nullable = false)
    private NguyenLieu nguyenLieu;
}

