package com.hoangtuan.coffee_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ban")
public class BanController {

    @GetMapping({"", "/", "/index"})
    public String index() {
        return "ban/index";
    }
}
