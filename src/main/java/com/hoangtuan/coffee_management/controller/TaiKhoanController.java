package com.hoangtuan.coffee_management.controller;

import com.hoangtuan.coffee_management.dto.ResetMatKhauDTO;
import com.hoangtuan.coffee_management.dto.TaiKhoanFormDTO;
import com.hoangtuan.coffee_management.service.NhanVienService;
import com.hoangtuan.coffee_management.service.TaiKhoanService;
import com.hoangtuan.coffee_management.service.VaiTroService;
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
@RequestMapping("/tai-khoan")
public class TaiKhoanController {

    private final TaiKhoanService taiKhoanService;
    private final NhanVienService nhanVienService;
    private final VaiTroService vaiTroService;

    public TaiKhoanController(
            TaiKhoanService taiKhoanService,
            NhanVienService nhanVienService,
            VaiTroService vaiTroService
    ) {
        this.taiKhoanService = taiKhoanService;
        this.nhanVienService = nhanVienService;
        this.vaiTroService = vaiTroService;
    }

    @GetMapping({"", "/", "/index"})
    public String index(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String vaiTro,
            Model model
    ) {
        model.addAttribute("danhSachTaiKhoan", taiKhoanService.getDanhSach(keyword, trangThai, vaiTro));
        model.addAttribute("keyword", keyword);
        model.addAttribute("trangThaiLoc", trangThai);
        model.addAttribute("vaiTroLoc", vaiTro);
        return "taikhoan/index";
    }

    @GetMapping("/them")
    public String them(Model model) {
        prepareCreateFormModel(model, taiKhoanService.getFormThem());
        return "taikhoan/form";
    }

    @PostMapping("/them")
    public String xuLyThem(
            @ModelAttribute("taiKhoanForm") TaiKhoanFormDTO taiKhoanForm,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            taiKhoanService.taoTaiKhoan(taiKhoanForm);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo tài khoản thành công.");
            return "redirect:/tai-khoan";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            prepareCreateFormModel(model, taiKhoanForm);
            return "taikhoan/form";
        }
    }

    @GetMapping("/sua/{maTaiKhoan}")
    public String sua(@PathVariable String maTaiKhoan, Model model) {
        TaiKhoanFormDTO form = taiKhoanService.getFormSua(maTaiKhoan);
        prepareEditFormModel(model, form);
        return "taikhoan/form";
    }

    @PostMapping("/sua/{maTaiKhoan}")
    public String xuLySua(
            @PathVariable String maTaiKhoan,
            @ModelAttribute("taiKhoanForm") TaiKhoanFormDTO taiKhoanForm,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            taiKhoanService.capNhatTaiKhoan(maTaiKhoan, taiKhoanForm);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật tài khoản thành công.");
            return "redirect:/tai-khoan";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            prepareEditFormModel(model, taiKhoanForm);
            return "taikhoan/form";
        }
    }

    @PostMapping("/khoa/{maTaiKhoan}")
    public String khoa(@PathVariable String maTaiKhoan, RedirectAttributes redirectAttributes, org.springframework.security.core.Authentication authentication) {
        try {
            taiKhoanService.khoaTaiKhoan(maTaiKhoan, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Đã khóa tài khoản.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/tai-khoan";
    }

    @PostMapping("/mo-khoa/{maTaiKhoan}")
    public String moKhoa(@PathVariable String maTaiKhoan, RedirectAttributes redirectAttributes) {
        try {
            taiKhoanService.moKhoaTaiKhoan(maTaiKhoan);
            redirectAttributes.addFlashAttribute("successMessage", "Đã mở khóa tài khoản.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/tai-khoan";
    }

    @GetMapping("/reset-mat-khau/{maTaiKhoan}")
    public String resetMatKhau(@PathVariable String maTaiKhoan, Model model) {
        model.addAttribute("taiKhoanForm", taiKhoanService.getFormSua(maTaiKhoan));
        model.addAttribute("resetMatKhau", new ResetMatKhauDTO());
        model.addAttribute("formAction", "/tai-khoan/reset-mat-khau/" + maTaiKhoan);
        return "taikhoan/reset-mat-khau";
    }

    @PostMapping("/reset-mat-khau/{maTaiKhoan}")
    public String xuLyResetMatKhau(
            @PathVariable String maTaiKhoan,
            @ModelAttribute("resetMatKhau") ResetMatKhauDTO resetMatKhauDTO,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            taiKhoanService.resetMatKhau(maTaiKhoan, resetMatKhauDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Reset mật khẩu thành công.");
            return "redirect:/tai-khoan";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("taiKhoanForm", taiKhoanService.getFormSua(maTaiKhoan));
            model.addAttribute("resetMatKhau", resetMatKhauDTO);
            model.addAttribute("formAction", "/tai-khoan/reset-mat-khau/" + maTaiKhoan);
            return "taikhoan/reset-mat-khau";
        }
    }

    private void prepareCreateFormModel(Model model, TaiKhoanFormDTO form) {
        model.addAttribute("taiKhoanForm", form);
        model.addAttribute("danhSachNhanVien", nhanVienService.getNhanVienChuaCoTaiKhoan());
        model.addAttribute("danhSachVaiTro", vaiTroService.getVaiTroCoDinh());
        model.addAttribute("formAction", "/tai-khoan/them");
        model.addAttribute("formMode", "create");
    }

    private void prepareEditFormModel(Model model, TaiKhoanFormDTO form) {
        model.addAttribute("taiKhoanForm", form);
        model.addAttribute("danhSachVaiTro", vaiTroService.getVaiTroCoDinh());
        model.addAttribute("formAction", "/tai-khoan/sua/" + form.getMaTaiKhoan());
        model.addAttribute("formMode", "edit");
    }
}
