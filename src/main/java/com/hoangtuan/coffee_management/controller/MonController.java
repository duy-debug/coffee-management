package com.hoangtuan.coffee_management.controller;

import com.hoangtuan.coffee_management.dto.MonFormDTO;
import com.hoangtuan.coffee_management.dto.MonSearchDTO;
import com.hoangtuan.coffee_management.service.MonService;
import com.hoangtuan.coffee_management.service.NhomMonService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/mon")
public class MonController {

    private final MonService monService;
    private final NhomMonService nhomMonService;

    public MonController(MonService monService, NhomMonService nhomMonService) {
        this.monService = monService;
        this.nhomMonService = nhomMonService;
    }

    @GetMapping({"", "/", "/index"})
    public String index(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String maNhomMon,
            @RequestParam(required = false) String trangThai,
            Model model
    ) {
        model.addAttribute("danhSachMon", monService.searchAndFilter(new MonSearchDTO(keyword, maNhomMon, trangThai), false));
        model.addAttribute("danhSachNhomMon", nhomMonService.findAll());
        model.addAttribute("keyword", keyword);
        model.addAttribute("maNhomMonLoc", maNhomMon);
        model.addAttribute("trangThaiLoc", trangThai);
        return "mon/index";
    }

    @GetMapping("/them")
    public String them(Model model) {
        model.addAttribute("monForm", monService.getFormThem());
        model.addAttribute("danhSachNhomMon", nhomMonService.findAll());
        model.addAttribute("formAction", "/mon/them");
        model.addAttribute("formMode", "create");
        return "mon/form";
    }

    @PostMapping("/them")
    public String xuLyThem(
            @ModelAttribute("monForm") MonFormDTO monForm,
            @RequestParam(value = "hinhAnhFile", required = false) MultipartFile hinhAnhFile,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            monService.save(monForm, hinhAnhFile);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm món thành công.");
            return "redirect:/mon";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("monForm", monForm);
            model.addAttribute("danhSachNhomMon", nhomMonService.findAll());
            model.addAttribute("formAction", "/mon/them");
            model.addAttribute("formMode", "create");
            return "mon/form";
        }
    }

    @GetMapping("/sua/{maMon}")
    public String sua(@PathVariable String maMon, Model model) {
        model.addAttribute("monForm", monService.getFormSua(maMon));
        model.addAttribute("danhSachNhomMon", nhomMonService.findAll());
        model.addAttribute("formAction", "/mon/sua/" + maMon);
        model.addAttribute("formMode", "edit");
        return "mon/form";
    }

    @PostMapping("/sua/{maMon}")
    public String xuLySua(
            @PathVariable String maMon,
            @ModelAttribute("monForm") MonFormDTO monForm,
            @RequestParam(value = "hinhAnhFile", required = false) MultipartFile hinhAnhFile,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            monService.update(maMon, monForm, hinhAnhFile);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật món thành công.");
            return "redirect:/mon";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("monForm", monForm);
            model.addAttribute("danhSachNhomMon", nhomMonService.findAll());
            model.addAttribute("formAction", "/mon/sua/" + maMon);
            model.addAttribute("formMode", "edit");
            return "mon/form";
        }
    }

    @PostMapping("/ngung-ban/{maMon}")
    public String ngungBan(@PathVariable String maMon, RedirectAttributes redirectAttributes) {
        try {
            monService.ngungBan(maMon);
            redirectAttributes.addFlashAttribute("successMessage", "Đã ngừng bán món.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/mon";
    }

    @PostMapping("/mo-ban/{maMon}")
    public String moBan(@PathVariable String maMon, RedirectAttributes redirectAttributes) {
        try {
            monService.moBan(maMon);
            redirectAttributes.addFlashAttribute("successMessage", "Đã mở bán lại món.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/mon";
    }
}
