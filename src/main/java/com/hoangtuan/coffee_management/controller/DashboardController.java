package com.hoangtuan.coffee_management.controller;

import com.hoangtuan.coffee_management.service.BaoCaoService;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final BaoCaoService baoCaoService;

    public DashboardController(BaoCaoService baoCaoService) {
        this.baoCaoService = baoCaoService;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("dashboard", baoCaoService.getDashboard());
        return "dashboard/index";
    }
}
