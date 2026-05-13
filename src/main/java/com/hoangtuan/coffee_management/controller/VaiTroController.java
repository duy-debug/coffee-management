package com.hoangtuan.coffee_management.controller;

import com.hoangtuan.coffee_management.repository.VaiTroRepository;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/vai-tro")
public class VaiTroController {

    private final VaiTroRepository vaiTroRepository;

    public VaiTroController(VaiTroRepository vaiTroRepository) {
        this.vaiTroRepository = vaiTroRepository;
    }

    @GetMapping({"", "/", "/index"})
    public String index(Model model) {
        model.addAttribute("danhSachVaiTro", vaiTroRepository.findAll());
        return "vaitro/index";
    }
}
