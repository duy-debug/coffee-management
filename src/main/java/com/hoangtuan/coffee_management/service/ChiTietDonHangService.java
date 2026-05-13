package com.hoangtuan.coffee_management.service;

import com.hoangtuan.coffee_management.entity.ChiTietDonHang;
import java.util.List;

public interface ChiTietDonHangService {

    List<ChiTietDonHang> findByDonHang(String maDonHang);
}
