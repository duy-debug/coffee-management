package com.hoangtuan.coffee_management.service;

import com.hoangtuan.coffee_management.dto.HoaDonThanhToanViewDTO;
import com.hoangtuan.coffee_management.dto.ThanhToanDTO;
import com.hoangtuan.coffee_management.entity.DonHang;
import com.hoangtuan.coffee_management.entity.HoaDon;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface HoaDonService {

    List<HoaDon> findAll(String keyword, String phuongThucThanhToan, LocalDate tuNgay, LocalDate denNgay);

    HoaDon findById(String maHoaDon);

    HoaDon findByDonHang(String maDonHang);

    List<DonHang> findDonHangChoThanhToan();

    HoaDonThanhToanViewDTO getThanhToanData(String maDonHang, String maKhuyenMai, String phuongThucThanhToan);

    HoaDon thanhToan(String maDonHang, ThanhToanDTO dto, String tenDangNhap);

    BigDecimal tinhTienGiam(BigDecimal tongTien, com.hoangtuan.coffee_management.entity.KhuyenMai khuyenMai);

    String generateNextMaHoaDon();
}
