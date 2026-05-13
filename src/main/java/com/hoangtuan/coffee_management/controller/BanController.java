package com.hoangtuan.coffee_management.controller;

import com.hoangtuan.coffee_management.dto.BanFormDTO;
import com.hoangtuan.coffee_management.dto.BanSearchDTO;
import com.hoangtuan.coffee_management.service.BanService;
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
@RequestMapping("/ban")
public class BanController {

    private final BanService banService;

    public BanController(BanService banService) {
        this.banService = banService;
    }

    @GetMapping({"", "/", "/index"})
    public String index(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String trangThai,
            Model model
    ) {
        model.addAttribute("danhSachBan", banService.searchAndFilter(new BanSearchDTO(keyword, trangThai)));
        model.addAttribute("keyword", keyword);
        model.addAttribute("trangThaiLoc", trangThai);
        return "ban/index";
    }

    @GetMapping("/trang-thai")
    public String trangThai() {
        return "redirect:/ban";
    }

    @GetMapping("/them")
    public String them(Model model) {
        model.addAttribute("banForm", banService.getFormThem());
        model.addAttribute("formAction", "/ban/them");
        model.addAttribute("formMode", "create");
        return "ban/form";
    }

    @PostMapping("/them")
    public String xuLyThem(
            @ModelAttribute("banForm") BanFormDTO banForm,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            banService.save(banForm);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm bàn thành công.");
            return "redirect:/ban";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("banForm", banForm);
            model.addAttribute("formAction", "/ban/them");
            model.addAttribute("formMode", "create");
            return "ban/form";
        }
    }

    @GetMapping("/sua/{maBan}")
    public String sua(@PathVariable String maBan, Model model) {
        model.addAttribute("banForm", banService.getFormSua(maBan));
        model.addAttribute("formAction", "/ban/sua/" + maBan);
        model.addAttribute("formMode", "edit");
        return "ban/form";
    }

    @PostMapping("/sua/{maBan}")
    public String xuLySua(
            @PathVariable String maBan,
            @ModelAttribute("banForm") BanFormDTO banForm,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            banService.update(maBan, banForm);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật bàn thành công.");
            return "redirect:/ban";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("banForm", banForm);
            model.addAttribute("formAction", "/ban/sua/" + maBan);
            model.addAttribute("formMode", "edit");
            return "ban/form";
        }
    }

    @PostMapping("/cap-nhat-trang-thai/{maBan}")
    public String capNhatTrangThai(
            @PathVariable String maBan,
            @RequestParam String trangThai,
            RedirectAttributes redirectAttributes
    ) {
        try {
            banService.updateTrangThai(maBan, trangThai);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái bàn thành công.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ban";
    }
}
