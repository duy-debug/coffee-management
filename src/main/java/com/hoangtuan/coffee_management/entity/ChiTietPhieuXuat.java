package com.hoangtuan.coffee_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "chi_tiet_phieu_xuat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietPhieuXuat {

    @Id
    @Column(name = "ma_ctpx", length = 10, nullable = false)
    private String maCTPX;

    @Column(name = "so_luong_xuat", nullable = false)
    private Integer soLuongXuat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_phieu_xuat", referencedColumnName = "ma_phieu_xuat", nullable = false)
    private PhieuXuatKho phieuXuatKho;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_nguyen_lieu", referencedColumnName = "ma_nguyen_lieu", nullable = false)
    private NguyenLieu nguyenLieu;
}

