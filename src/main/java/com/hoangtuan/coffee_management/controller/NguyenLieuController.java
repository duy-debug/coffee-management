package com.hoangtuan.coffee_management.controller;

import com.hoangtuan.coffee_management.dto.NguyenLieuFormDTO;
import com.hoangtuan.coffee_management.dto.NguyenLieuSearchDTO;
import com.hoangtuan.coffee_management.service.NguyenLieuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/nguyen-lieu")
public class NguyenLieuController {

    private final NguyenLieuService nguyenLieuService;

    public NguyenLieuController(NguyenLieuService nguyenLieuService) {
        this.nguyenLieuService = nguyenLieuService;
    }

    @GetMapping({"", "/", "/index"})
    public String index(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sapHet,
            Model model
    ) {
        Boolean locSapHet = null;
        if ("true".equalsIgnoreCase(sapHet) || "false".equalsIgnoreCase(sapHet)) {
            locSapHet = Boolean.valueOf(sapHet);
        }
        model.addAttribute("danhSachNguyenLieu", nguyenLieuService.getDanhSach(new NguyenLieuSearchDTO(keyword, locSapHet)));
        model.addAttribute("keyword", keyword);
        model.addAttribute("sapHetLoc", sapHet);
        return "nguyenlieu/index";
    }

    @GetMapping("/them")
    public String them(Model model) {
        model.addAttribute("nguyenLieuForm", nguyenLieuService.getFormThem());
        model.addAttribute("formAction", "/nguyen-lieu/them");
        model.addAttribute("formMode", "create");
        return "nguyenlieu/form";
    }

    @PostMapping("/them")
    public String xuLyThem(
            @ModelAttribute("nguyenLieuForm") NguyenLieuFormDTO nguyenLieuForm,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            nguyenLieuService.save(nguyenLieuForm);
            redirectAttributes.addFlashAttribute("successMessage", "Them nguyen lieu thanh cong.");
            return "redirect:/nguyen-lieu";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("nguyenLieuForm", nguyenLieuForm);
            model.addAttribute("formAction", "/nguyen-lieu/them");
            model.addAttribute("formMode", "create");
            return "nguyenlieu/form";
        }
    }

    @GetMapping("/sua/{maNguyenLieu}")
    public String sua(@PathVariable String maNguyenLieu, Model model) {
        model.addAttribute("nguyenLieuForm", nguyenLieuService.getFormSua(maNguyenLieu));
        model.addAttribute("formAction", "/nguyen-lieu/sua/" + maNguyenLieu);
        model.addAttribute("formMode", "edit");
        return "nguyenlieu/form";
    }

    @PostMapping("/sua/{maNguyenLieu}")
    public String xuLySua(
            @PathVariable String maNguyenLieu,
            @ModelAttribute("nguyenLieuForm") NguyenLieuFormDTO nguyenLieuForm,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            nguyenLieuService.update(maNguyenLieu, nguyenLieuForm);
            redirectAttributes.addFlashAttribute("successMessage", "Cap nhat nguyen lieu thanh cong.");
            return "redirect:/nguyen-lieu";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("nguyenLieuForm", nguyenLieuForm);
            model.addAttribute("formAction", "/nguyen-lieu/sua/" + maNguyenLieu);
            model.addAttribute("formMode", "edit");
            return "nguyenlieu/form";
        }
    }

    @GetMapping("/canh-bao")
    public String canhBao(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("danhSachNguyenLieu", nguyenLieuService.getDanhSach(new NguyenLieuSearchDTO(keyword, Boolean.TRUE)));
        model.addAttribute("keyword", keyword);
        return "nguyenlieu/canh-bao";
    }

    @GetMapping("/{maNguyenLieu:NL\\d+}")
    public String detail(@PathVariable String maNguyenLieu, Model model) {
        model.addAttribute("nguyenLieuDetail", nguyenLieuService.getChiTiet(maNguyenLieu));
        return "nguyenlieu/detail";
    }
}
