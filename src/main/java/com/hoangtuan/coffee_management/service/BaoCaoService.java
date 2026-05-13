package com.hoangtuan.coffee_management.service;

import com.hoangtuan.coffee_management.dto.BaoCaoDoanhThuDTO;
import com.hoangtuan.coffee_management.dto.BaoCaoMonBanChayDTO;
import com.hoangtuan.coffee_management.dto.BaoCaoTonKhoDTO;
import com.hoangtuan.coffee_management.dto.BaoCaoTongQuanDTO;
import com.hoangtuan.coffee_management.dto.DashboardDTO;
import java.time.LocalDate;

public interface BaoCaoService {

    BaoCaoTongQuanDTO getTongQuanBaoCao();

    BaoCaoDoanhThuDTO getBaoCaoDoanhThu(LocalDate tuNgay, LocalDate denNgay);

    BaoCaoMonBanChayDTO getBaoCaoMonBanChay(LocalDate tuNgay, LocalDate denNgay);

    BaoCaoTonKhoDTO getBaoCaoTonKho();

    DashboardDTO getDashboard();
}
