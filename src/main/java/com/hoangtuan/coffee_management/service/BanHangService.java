package com.hoangtuan.coffee_management.service;

import com.hoangtuan.coffee_management.dto.BanHangPageDTO;
import com.hoangtuan.coffee_management.dto.BanHangRequestDTO;
import com.hoangtuan.coffee_management.entity.DonHang;
import jakarta.servlet.http.HttpSession;

public interface BanHangService {

    BanHangPageDTO getBanHangPageData(String keyword, String maNhomMon, String loaiDonHang, String maBan, String maKhachHang, HttpSession session);

    void themMonVaoGio(String maMon, Integer soLuong, HttpSession session);

    void capNhatSoLuong(String maMon, Integer soLuong, HttpSession session);

    void xoaMon(String maMon, HttpSession session);

    DonHang taoDonHang(BanHangRequestDTO dto, String tenDangNhap, HttpSession session);

    void huyDon(String maDonHang);
}
