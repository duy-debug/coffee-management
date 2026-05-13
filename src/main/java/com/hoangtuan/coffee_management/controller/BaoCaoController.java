package com.hoangtuan.coffee_management.controller;

import com.hoangtuan.coffee_management.dto.BaoCaoDoanhThuDTO;
import com.hoangtuan.coffee_management.dto.BaoCaoMonBanChayDTO;
import com.hoangtuan.coffee_management.dto.BaoCaoTonKhoDTO;
import com.hoangtuan.coffee_management.dto.DashboardDTO;
import com.hoangtuan.coffee_management.service.BaoCaoService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/bao-cao")
public class BaoCaoController {

    private final BaoCaoService baoCaoService;

    public BaoCaoController(BaoCaoService baoCaoService) {
        this.baoCaoService = baoCaoService;
    }

    @GetMapping({"", "/", "/index"})
    public String index(Model model) {
        model.addAttribute("tongQuanBaoCao", baoCaoService.getTongQuanBaoCao());
        return "baocao/index";
    }

    @GetMapping("/doanh-thu")
    public String doanhThu(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate tuNgay,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate denNgay,
            Model model
    ) {
        LocalDate[] range = resolveRange(tuNgay, denNgay);
        if (range == null) {
            model.addAttribute("errorMessage", "Tu ngay khong duoc sau den ngay.");
            model.addAttribute("baoCaoDoanhThu", new BaoCaoDoanhThuDTO(
                    null,
                    null,
                    Collections.emptyList(),
                    0L,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            ));
            return "baocao/doanh-thu";
        }
        BaoCaoDoanhThuDTO baoCaoDoanhThu = baoCaoService.getBaoCaoDoanhThu(range[0], range[1]);
        model.addAttribute("baoCaoDoanhThu", baoCaoDoanhThu);
        return "baocao/doanh-thu";
    }

    @GetMapping("/mon-ban-chay")
    public String monBanChay(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate tuNgay,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate denNgay,
            Model model
    ) {
        LocalDate[] range = resolveRange(tuNgay, denNgay);
        if (range == null) {
            model.addAttribute("errorMessage", "Tu ngay khong duoc sau den ngay.");
            model.addAttribute("baoCaoMonBanChay", new BaoCaoMonBanChayDTO(
                    null,
                    null,
                    Collections.emptyList(),
                    0L,
                    BigDecimal.ZERO
            ));
            return "baocao/mon-ban-chay";
        }
        BaoCaoMonBanChayDTO baoCaoMonBanChay = baoCaoService.getBaoCaoMonBanChay(range[0], range[1]);
        model.addAttribute("baoCaoMonBanChay", baoCaoMonBanChay);
        return "baocao/mon-ban-chay";
    }

    @GetMapping("/ton-kho")
    public String tonKho(Model model) {
        BaoCaoTonKhoDTO baoCaoTonKho = baoCaoService.getBaoCaoTonKho();
        model.addAttribute("baoCaoTonKho", baoCaoTonKho);
        return "baocao/ton-kho";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        DashboardDTO dashboard = baoCaoService.getDashboard();
        model.addAttribute("dashboard", dashboard);
        return "dashboard/index";
    }

    private LocalDate[] resolveRange(LocalDate tuNgay, LocalDate denNgay) {
        LocalDate start = tuNgay != null ? tuNgay : LocalDate.now().withDayOfMonth(1);
        LocalDate end = denNgay != null ? denNgay : LocalDate.now();
        if (start.isAfter(end)) {
            return null;
        }
        return new LocalDate[]{start, end};
    }
}
