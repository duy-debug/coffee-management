package com.hoangtuan.coffee_management.controller;

import com.hoangtuan.coffee_management.dto.ChiTietPhieuNhapFormDTO;
import com.hoangtuan.coffee_management.dto.PhieuNhapKhoFormDTO;
import com.hoangtuan.coffee_management.service.NguyenLieuService;
import com.hoangtuan.coffee_management.service.PhieuNhapKhoService;
import java.util.Collections;
import java.util.List;
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
@RequestMapping("/phieu-nhap-kho")
public class PhieuNhapKhoController {

    private final PhieuNhapKhoService phieuNhapKhoService;
    private final NguyenLieuService nguyenLieuService;

    public PhieuNhapKhoController(PhieuNhapKhoService phieuNhapKhoService, NguyenLieuService nguyenLieuService) {
        this.phieuNhapKhoService = phieuNhapKhoService;
        this.nguyenLieuService = nguyenLieuService;
    }

    @GetMapping({"", "/", "/index"})
    public String index(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("danhSachPhieuNhap", phieuNhapKhoService.search(keyword));
        model.addAttribute("keyword", keyword);
        return "phieunhapkho/index";
    }

    @GetMapping("/them")
    public String them(Model model) {
        prepareForm(model, defaultForm(), "/phieu-nhap-kho/them", "create", null);
        return "phieunhapkho/form";
    }

    @PostMapping("/them")
    public String xuLyThem(
            @ModelAttribute("phieuNhapForm") PhieuNhapKhoFormDTO phieuNhapForm,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            phieuNhapKhoService.createPhieuNhap(phieuNhapForm, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Them phieu nhap kho thanh cong.");
            return "redirect:/phieu-nhap-kho";
        } catch (IllegalArgumentException ex) {
            prepareForm(model, phieuNhapForm, "/phieu-nhap-kho/them", "create", ex.getMessage());
            return "phieunhapkho/form";
        }
    }

    @GetMapping("/{maPhieuNhap}")
    public String detail(@PathVariable String maPhieuNhap, Model model) {
        model.addAttribute("phieuNhapDetail", phieuNhapKhoService.getChiTiet(maPhieuNhap));
        return "phieunhapkho/detail";
    }

    private PhieuNhapKhoFormDTO defaultForm() {
        return new PhieuNhapKhoFormDTO(
                phieuNhapKhoService.generateNextMaPhieuNhap(),
                "",
                Collections.singletonList(new ChiTietPhieuNhapFormDTO("", 1, java.math.BigDecimal.ZERO))
        );
    }

    private void prepareForm(Model model, PhieuNhapKhoFormDTO form, String formAction, String formMode, String errorMessage) {
        if (form.getDanhSachChiTiet() == null || form.getDanhSachChiTiet().isEmpty()) {
            form.setDanhSachChiTiet(Collections.singletonList(new ChiTietPhieuNhapFormDTO("", 1, java.math.BigDecimal.ZERO)));
        }
        model.addAttribute("phieuNhapForm", form);
        model.addAttribute("formAction", formAction);
        model.addAttribute("formMode", formMode);
        model.addAttribute("danhSachNguyenLieu", nguyenLieuService.findAll());
        model.addAttribute("errorMessage", errorMessage);
    }
}
