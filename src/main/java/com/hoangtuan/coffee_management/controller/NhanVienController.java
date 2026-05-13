package com.hoangtuan.coffee_management.controller;

import com.hoangtuan.coffee_management.dto.NhanVienFormDTO;
import com.hoangtuan.coffee_management.service.NhanVienService;
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
@RequestMapping("/nhan-vien")
public class NhanVienController {

    private final NhanVienService nhanVienService;

    public NhanVienController(NhanVienService nhanVienService) {
        this.nhanVienService = nhanVienService;
    }

    @GetMapping({"", "/", "/index"})
    public String index(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String trangThai,
            Model model
    ) {
        model.addAttribute("danhSachNhanVien", nhanVienService.getDanhSach(keyword, trangThai));
        model.addAttribute("keyword", keyword);
        model.addAttribute("trangThaiLoc", trangThai);
        return "nhanvien/index";
    }

    @GetMapping("/them")
    public String them(Model model) {
        model.addAttribute("nhanVienForm", nhanVienService.getFormThem());
        model.addAttribute("formAction", "/nhan-vien/them");
        model.addAttribute("formMode", "create");
        return "nhanvien/form";
    }

    @PostMapping("/them")
    public String xuLyThem(
            @ModelAttribute("nhanVienForm") NhanVienFormDTO nhanVienForm,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            nhanVienService.taoNhanVien(nhanVienForm);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm nhân viên thành công.");
            return "redirect:/nhan-vien";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("formAction", "/nhan-vien/them");
            model.addAttribute("formMode", "create");
            model.addAttribute("nhanVienForm", nhanVienForm);
            return "nhanvien/form";
        }
    }

    @GetMapping("/sua/{maNhanVien}")
    public String sua(@PathVariable String maNhanVien, Model model) {
        model.addAttribute("nhanVienForm", nhanVienService.getFormSua(maNhanVien));
        model.addAttribute("formAction", "/nhan-vien/sua/" + maNhanVien);
        model.addAttribute("formMode", "edit");
        return "nhanvien/form";
    }

    @PostMapping("/sua/{maNhanVien}")
    public String xuLySua(
            @PathVariable String maNhanVien,
            @ModelAttribute("nhanVienForm") NhanVienFormDTO nhanVienForm,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            nhanVienService.capNhatNhanVien(maNhanVien, nhanVienForm);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật nhân viên thành công.");
            return "redirect:/nhan-vien";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("formAction", "/nhan-vien/sua/" + maNhanVien);
            model.addAttribute("formMode", "edit");
            model.addAttribute("nhanVienForm", nhanVienForm);
            return "nhanvien/form";
        }
    }

    @PostMapping("/ngung/{maNhanVien}")
    public String ngung(@PathVariable String maNhanVien, RedirectAttributes redirectAttributes) {
        try {
            nhanVienService.ngungNhanVien(maNhanVien);
            redirectAttributes.addFlashAttribute("successMessage", "Đã ngừng làm nhân viên.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/nhan-vien";
    }

    @PostMapping("/kich-hoat/{maNhanVien}")
    public String kichHoat(@PathVariable String maNhanVien, RedirectAttributes redirectAttributes) {
        try {
            nhanVienService.kichHoatNhanVien(maNhanVien);
            redirectAttributes.addFlashAttribute("successMessage", "Đã kích hoạt lại nhân viên.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/nhan-vien";
    }
}
