package com.hoangtuan.coffee_management.controller;

import com.hoangtuan.coffee_management.dto.KhachHangFormDTO;
import com.hoangtuan.coffee_management.service.KhachHangService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/khach-hang")
public class KhachHangController {

    private final KhachHangService khachHangService;

    public KhachHangController(KhachHangService khachHangService) {
        this.khachHangService = khachHangService;
    }

    @GetMapping({"", "/", "/index"})
    public String index(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("danhSachKhachHang", khachHangService.search(keyword));
        model.addAttribute("keyword", keyword);
        return "khachhang/index";
    }

    @GetMapping("/them")
    public String them(Model model) {
        model.addAttribute("khachHangForm", khachHangService.getFormThem());
        model.addAttribute("formAction", "/khach-hang/them");
        model.addAttribute("formMode", "create");
        return "khachhang/form";
    }

    @PostMapping("/them")
    public String xuLyThem(
            @ModelAttribute("khachHangForm") KhachHangFormDTO khachHangForm,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            khachHangService.save(khachHangForm);
            redirectAttributes.addFlashAttribute("successMessage", "Them khach hang thanh cong.");
            return "redirect:/khach-hang";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("khachHangForm", khachHangForm);
            model.addAttribute("formAction", "/khach-hang/them");
            model.addAttribute("formMode", "create");
            return "khachhang/form";
        }
    }

    @GetMapping("/sua/{maKhachHang}")
    public String sua(@PathVariable String maKhachHang, Model model) {
        model.addAttribute("khachHangForm", khachHangService.getFormSua(maKhachHang));
        model.addAttribute("formAction", "/khach-hang/sua/" + maKhachHang);
        model.addAttribute("formMode", "edit");
        return "khachhang/form";
    }

    @PostMapping("/sua/{maKhachHang}")
    public String xuLySua(
            @PathVariable String maKhachHang,
            @ModelAttribute("khachHangForm") KhachHangFormDTO khachHangForm,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            khachHangService.update(maKhachHang, khachHangForm);
            redirectAttributes.addFlashAttribute("successMessage", "Cap nhat khach hang thanh cong.");
            return "redirect:/khach-hang";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("khachHangForm", khachHangForm);
            model.addAttribute("formAction", "/khach-hang/sua/" + maKhachHang);
            model.addAttribute("formMode", "edit");
            return "khachhang/form";
        }
    }

    @GetMapping("/{maKhachHang}")
    public String detail(@PathVariable String maKhachHang, Model model) {
        model.addAttribute("khachHangDetail", khachHangService.getChiTiet(maKhachHang));
        return "khachhang/detail";
    }

    @GetMapping("/lich-su/{maKhachHang}")
    public String lichSu(@PathVariable String maKhachHang, Model model) {
        model.addAttribute("khachHang", khachHangService.findById(maKhachHang));
        model.addAttribute("lichSuMuaHangs", khachHangService.getLichSuMuaHang(maKhachHang));
        return "khachhang/lich-su";
    }
}
