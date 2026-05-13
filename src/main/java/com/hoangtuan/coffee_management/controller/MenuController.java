package com.hoangtuan.coffee_management.controller;

import com.hoangtuan.coffee_management.dto.MonSearchDTO;
import com.hoangtuan.coffee_management.service.MonService;
import com.hoangtuan.coffee_management.service.NhomMonService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/menu")
public class MenuController {

    private final MonService monService;
    private final NhomMonService nhomMonService;

    public MenuController(MonService monService, NhomMonService nhomMonService) {
        this.monService = monService;
        this.nhomMonService = nhomMonService;
    }

    @GetMapping({"", "/", "/index"})
    public String index(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String maNhomMon,
            Model model
    ) {
        model.addAttribute("danhSachMon", monService.searchAndFilter(new MonSearchDTO(keyword, maNhomMon, "true"), true));
        model.addAttribute("danhSachNhomMon", nhomMonService.findAll());
        model.addAttribute("keyword", keyword);
        model.addAttribute("maNhomMonLoc", maNhomMon);
        return "menu/index";
    }
}
