package com.hoangtuan.coffee_management.controller;

import com.hoangtuan.coffee_management.dto.KhuyenMaiFormDTO;
import com.hoangtuan.coffee_management.service.KhuyenMaiService;
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
@RequestMapping("/khuyen-mai")
public class KhuyenMaiController {

    private final KhuyenMaiService khuyenMaiService;

    public KhuyenMaiController(KhuyenMaiService khuyenMaiService) {
        this.khuyenMaiService = khuyenMaiService;
    }

    @GetMapping({"", "/", "/index"})
    public String index(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String loaiKhuyenMai,
            @RequestParam(required = false) String trangThai,
            Model model
    ) {
        Boolean trangThaiLoc = null;
        if ("true".equalsIgnoreCase(trangThai) || "false".equalsIgnoreCase(trangThai)) {
            trangThaiLoc = Boolean.valueOf(trangThai);
        }
        model.addAttribute("danhSachKhuyenMai", khuyenMaiService.getDanhSach(keyword, loaiKhuyenMai, trangThaiLoc));
        model.addAttribute("keyword", keyword);
        model.addAttribute("loaiKhuyenMaiLoc", loaiKhuyenMai);
        model.addAttribute("trangThaiLoc", trangThai);
        return "khuyenmai/index";
    }

    @GetMapping("/them")
    public String them(Model model) {
        model.addAttribute("khuyenMaiForm", khuyenMaiService.getFormThem());
        model.addAttribute("formAction", "/khuyen-mai/them");
        model.addAttribute("formMode", "create");
        return "khuyenmai/form";
    }

    @PostMapping("/them")
    public String xuLyThem(
            @ModelAttribute("khuyenMaiForm") KhuyenMaiFormDTO khuyenMaiForm,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            khuyenMaiService.save(khuyenMaiForm);
            redirectAttributes.addFlashAttribute("successMessage", "Them khuyen mai thanh cong.");
            return "redirect:/khuyen-mai";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("khuyenMaiForm", khuyenMaiForm);
            model.addAttribute("formAction", "/khuyen-mai/them");
            model.addAttribute("formMode", "create");
            return "khuyenmai/form";
        }
    }

    @GetMapping("/sua/{maKhuyenMai}")
    public String sua(@PathVariable String maKhuyenMai, Model model) {
        model.addAttribute("khuyenMaiForm", khuyenMaiService.getFormSua(maKhuyenMai));
        model.addAttribute("formAction", "/khuyen-mai/sua/" + maKhuyenMai);
        model.addAttribute("formMode", "edit");
        return "khuyenmai/form";
    }

    @PostMapping("/sua/{maKhuyenMai}")
    public String xuLySua(
            @PathVariable String maKhuyenMai,
            @ModelAttribute("khuyenMaiForm") KhuyenMaiFormDTO khuyenMaiForm,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            khuyenMaiService.update(maKhuyenMai, khuyenMaiForm);
            redirectAttributes.addFlashAttribute("successMessage", "Cap nhat khuyen mai thanh cong.");
            return "redirect:/khuyen-mai";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("khuyenMaiForm", khuyenMaiForm);
            model.addAttribute("formAction", "/khuyen-mai/sua/" + maKhuyenMai);
            model.addAttribute("formMode", "edit");
            return "khuyenmai/form";
        }
    }

    @PostMapping("/ngung/{maKhuyenMai}")
    public String ngung(@PathVariable String maKhuyenMai, RedirectAttributes redirectAttributes) {
        try {
            khuyenMaiService.ngungApDung(maKhuyenMai);
            redirectAttributes.addFlashAttribute("successMessage", "Da ngung ap dung khuyen mai.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/khuyen-mai";
    }

    @PostMapping("/kich-hoat/{maKhuyenMai}")
    public String kichHoat(@PathVariable String maKhuyenMai, RedirectAttributes redirectAttributes) {
        try {
            khuyenMaiService.kichHoat(maKhuyenMai);
            redirectAttributes.addFlashAttribute("successMessage", "Da kich hoat lai khuyen mai.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/khuyen-mai";
    }

    @GetMapping("/{maKhuyenMai}")
    public String detail(@PathVariable String maKhuyenMai, Model model) {
        model.addAttribute("khuyenMaiDetail", khuyenMaiService.getChiTiet(maKhuyenMai));
        return "khuyenmai/detail";
    }
}
