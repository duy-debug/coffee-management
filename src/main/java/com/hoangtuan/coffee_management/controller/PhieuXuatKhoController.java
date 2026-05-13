package com.hoangtuan.coffee_management.controller;

import com.hoangtuan.coffee_management.dto.ChiTietPhieuXuatFormDTO;
import com.hoangtuan.coffee_management.dto.PhieuXuatKhoFormDTO;
import com.hoangtuan.coffee_management.service.NguyenLieuService;
import com.hoangtuan.coffee_management.service.PhieuXuatKhoService;
import java.math.BigDecimal;
import java.util.Collections;
import org.springframework.security.core.Authentication;
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
@RequestMapping("/phieu-xuat-kho")
public class PhieuXuatKhoController {

    private final PhieuXuatKhoService phieuXuatKhoService;
    private final NguyenLieuService nguyenLieuService;

    public PhieuXuatKhoController(PhieuXuatKhoService phieuXuatKhoService, NguyenLieuService nguyenLieuService) {
        this.phieuXuatKhoService = phieuXuatKhoService;
        this.nguyenLieuService = nguyenLieuService;
    }

    @GetMapping({"", "/", "/index"})
    public String index(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("danhSachPhieuXuat", phieuXuatKhoService.search(keyword));
        model.addAttribute("keyword", keyword);
        return "phieuxuatkho/index";
    }

    @GetMapping("/them")
    public String them(Model model) {
        prepareForm(model, defaultForm(), "/phieu-xuat-kho/them", "create", null);
        return "phieuxuatkho/form";
    }

    @PostMapping("/them")
    public String xuLyThem(
            @ModelAttribute("phieuXuatForm") PhieuXuatKhoFormDTO phieuXuatForm,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            phieuXuatKhoService.createPhieuXuat(phieuXuatForm, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Them phieu xuat kho thanh cong.");
            return "redirect:/phieu-xuat-kho";
        } catch (IllegalArgumentException ex) {
            prepareForm(model, phieuXuatForm, "/phieu-xuat-kho/them", "create", ex.getMessage());
            return "phieuxuatkho/form";
        }
    }

    @GetMapping("/{maPhieuXuat}")
    public String detail(@PathVariable String maPhieuXuat, Model model) {
        model.addAttribute("phieuXuatDetail", phieuXuatKhoService.getChiTiet(maPhieuXuat));
        return "phieuxuatkho/detail";
    }

    private PhieuXuatKhoFormDTO defaultForm() {
        return new PhieuXuatKhoFormDTO(
                phieuXuatKhoService.generateNextMaPhieuXuat(),
                "",
                Collections.singletonList(new ChiTietPhieuXuatFormDTO("", null))
        );
    }

    private void prepareForm(Model model, PhieuXuatKhoFormDTO form, String formAction, String formMode, String errorMessage) {
        if (form.getDanhSachChiTiet() == null || form.getDanhSachChiTiet().isEmpty()) {
            form.setDanhSachChiTiet(Collections.singletonList(new ChiTietPhieuXuatFormDTO("", null)));
        }
        model.addAttribute("phieuXuatForm", form);
        model.addAttribute("formAction", formAction);
        model.addAttribute("formMode", formMode);
        model.addAttribute("danhSachNguyenLieu", nguyenLieuService.findAll());
        model.addAttribute("errorMessage", errorMessage);
    }
}
