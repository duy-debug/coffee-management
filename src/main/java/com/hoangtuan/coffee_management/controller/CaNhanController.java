package com.hoangtuan.coffee_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ca-nhan")
public class CaNhanController {

    @GetMapping({"", "/", "/index"})
    public String index() {
        return "canhan/index";
    }

    @GetMapping("/doi-mat-khau")
    public String doiMatKhau() {
        return "canhan/doi-mat-khau";
    }
}
