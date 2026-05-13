package com.hoangtuan.coffee_management.controller;

import com.hoangtuan.coffee_management.dto.NhomMonFormDTO;
import com.hoangtuan.coffee_management.service.NhomMonService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/nhom-mon")
public class NhomMonController {

    private final NhomMonService nhomMonService;

    public NhomMonController(NhomMonService nhomMonService) {
        this.nhomMonService = nhomMonService;
    }

    @GetMapping({"", "/", "/index"})
    public String index(Model model) {
        model.addAttribute("danhSachNhomMon", nhomMonService.findAll());
        return "nhommon/index";
    }

    @GetMapping("/them")
    public String them(Model model) {
        model.addAttribute("nhomMonForm", nhomMonService.getFormThem());
        model.addAttribute("formAction", "/nhom-mon/them");
        model.addAttribute("formMode", "create");
        return "nhommon/form";
    }

    @PostMapping("/them")
    public String xuLyThem(
            @ModelAttribute("nhomMonForm") NhomMonFormDTO nhomMonForm,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            nhomMonService.save(nhomMonForm);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm nhóm món thành công.");
            return "redirect:/nhom-mon";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("nhomMonForm", nhomMonForm);
            model.addAttribute("formAction", "/nhom-mon/them");
            model.addAttribute("formMode", "create");
            return "nhommon/form";
        }
    }

    @GetMapping("/sua/{maNhomMon}")
    public String sua(@PathVariable String maNhomMon, Model model) {
        model.addAttribute("nhomMonForm", nhomMonService.getFormSua(maNhomMon));
        model.addAttribute("formAction", "/nhom-mon/sua/" + maNhomMon);
        model.addAttribute("formMode", "edit");
        return "nhommon/form";
    }

    @PostMapping("/sua/{maNhomMon}")
    public String xuLySua(
            @PathVariable String maNhomMon,
            @ModelAttribute("nhomMonForm") NhomMonFormDTO nhomMonForm,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            nhomMonService.update(maNhomMon, nhomMonForm);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật nhóm món thành công.");
            return "redirect:/nhom-mon";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("nhomMonForm", nhomMonForm);
            model.addAttribute("formAction", "/nhom-mon/sua/" + maNhomMon);
            model.addAttribute("formMode", "edit");
            return "nhommon/form";
        }
    }
}
