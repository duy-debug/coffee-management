package com.hoangtuan.coffee_management.controller;

import com.hoangtuan.coffee_management.dto.CapNhatCaNhanDTO;
import com.hoangtuan.coffee_management.dto.DoiMatKhauDTO;
import com.hoangtuan.coffee_management.dto.ThongTinCaNhanDTO;
import com.hoangtuan.coffee_management.service.CaNhanService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ca-nhan")
public class CaNhanController {

    private final CaNhanService caNhanService;

    public CaNhanController(CaNhanService caNhanService) {
        this.caNhanService = caNhanService;
    }

    @GetMapping({"", "/", "/index"})
    public String index(Authentication authentication, Model model) {
        ThongTinCaNhanDTO thongTinCaNhan = caNhanService.getCurrentUserProfile(authentication.getName());
        model.addAttribute("thongTinCaNhan", thongTinCaNhan);
        return "canhan/index";
    }

    @GetMapping("/cap-nhat")
    public String capNhat(Authentication authentication, Model model) {
        ThongTinCaNhanDTO thongTinCaNhan = caNhanService.getCurrentUserProfile(authentication.getName());
        CapNhatCaNhanDTO capNhatCaNhan = new CapNhatCaNhanDTO(
                thongTinCaNhan.getHoTen(),
                thongTinCaNhan.getSoDienThoai(),
                thongTinCaNhan.getDiaChi()
        );
        model.addAttribute("capNhatCaNhan", capNhatCaNhan);
        return "canhan/cap-nhat";
    }

    @PostMapping("/cap-nhat")
    public String xuLyCapNhat(
            Authentication authentication,
            @ModelAttribute("capNhatCaNhan") CapNhatCaNhanDTO capNhatCaNhan,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            caNhanService.updateCurrentUserProfile(authentication.getName(), capNhatCaNhan);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin cá nhân thành công.");
            return "redirect:/ca-nhan";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "canhan/cap-nhat";
        }
    }

    @GetMapping("/doi-mat-khau")
    public String doiMatKhau(Model model) {
        model.addAttribute("doiMatKhau", new DoiMatKhauDTO());
        return "canhan/doi-mat-khau";
    }

    @PostMapping("/doi-mat-khau")
    public String xuLyDoiMatKhau(
            Authentication authentication,
            @ModelAttribute("doiMatKhau") DoiMatKhauDTO doiMatKhau,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            caNhanService.changePassword(authentication.getName(), doiMatKhau);
            redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công.");
            return "redirect:/ca-nhan";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "canhan/doi-mat-khau";
        }
    }
}
