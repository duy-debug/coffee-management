package com.hoangtuan.coffee_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/phieu-nhap-kho")
public class PhieuNhapKhoController {

    @GetMapping({"", "/", "/index"})
    public String index() {
        return "phieunhapkho/index";
    }
}
