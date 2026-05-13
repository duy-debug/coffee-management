package com.hoangtuan.coffee_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "phieu_xuat_kho")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhieuXuatKho {

    @Id
    @Column(name = "ma_phieu_xuat", length = 10, nullable = false)
    private String maPhieuXuat;

    @Column(name = "ngay_xuat", nullable = false)
    private LocalDateTime ngayXuat;

    @Column(name = "ly_do_xuat", length = 255)
    private String lyDoXuat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ma_nhan_vien", referencedColumnName = "ma_nhan_vien", nullable = false)
    private NhanVien nhanVien;
}

