package com.hoangtuan.coffee_management.service;

import com.hoangtuan.coffee_management.dto.BanHangItemDTO;
import com.hoangtuan.coffee_management.dto.BanHangRequestDTO;
import com.hoangtuan.coffee_management.entity.ChiTietDonHang;
import com.hoangtuan.coffee_management.entity.DonHang;
import java.time.LocalDate;
import java.util.List;

public interface DonHangService {

    DonHang findById(String maDonHang);

    List<DonHang> findAll(String keyword, String trangThai, String loaiDonHang, LocalDate tuNgay, LocalDate denNgay);

    DonHang createDonHang(BanHangRequestDTO requestDTO, String tenDangNhap, List<BanHangItemDTO> gioHang);

    void updateTongTien(String maDonHang);

    void updateTrangThai(String maDonHang, String trangThai);

    List<ChiTietDonHang> findChiTietByDonHang(String maDonHang);

    boolean kiemTraCoTheSua(DonHang donHang);

    void themMon(String maDonHang, String maMon, Integer soLuong);

    void capNhatSoLuong(String maCTDH, Integer soLuong);

    void xoaMon(String maCTDH);

    void capNhatTrangThai(String maDonHang, String trangThai);

    void huyDon(String maDonHang);

    void capNhatTongTien(String maDonHang);

    String findMaDonHangByChiTiet(String maCTDH);
}
