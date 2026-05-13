package com.hoangtuan.coffee_management.service;

import com.hoangtuan.coffee_management.dto.BanHangItemDTO;
import com.hoangtuan.coffee_management.dto.BanHangRequestDTO;
import com.hoangtuan.coffee_management.entity.ChiTietDonHang;
import com.hoangtuan.coffee_management.entity.DonHang;
import java.util.List;

public interface DonHangService {

    DonHang findById(String maDonHang);

    DonHang createDonHang(BanHangRequestDTO requestDTO, String tenDangNhap, List<BanHangItemDTO> gioHang);

    void updateTongTien(String maDonHang);

    void updateTrangThai(String maDonHang, String trangThai);

    List<ChiTietDonHang> findChiTietByDonHang(String maDonHang);
}
