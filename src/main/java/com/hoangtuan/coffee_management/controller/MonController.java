package com.hoangtuan.coffee_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mon")
public class MonController {

    @GetMapping({"", "/", "/index"})
    public String index() {
        return "mon/index";
    }
}
