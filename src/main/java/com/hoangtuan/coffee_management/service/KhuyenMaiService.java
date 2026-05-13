package com.hoangtuan.coffee_management.service;

import com.hoangtuan.coffee_management.entity.KhuyenMai;
import java.util.List;

public interface KhuyenMaiService {

    List<KhuyenMai> findKhuyenMaiConHieuLuc();

    KhuyenMai findById(String maKhuyenMai);
}
