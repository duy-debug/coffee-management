package com.hoangtuan.coffee_management.controller;

import com.hoangtuan.coffee_management.dto.MonSearchDTO;
import com.hoangtuan.coffee_management.entity.DonHang;
import com.hoangtuan.coffee_management.service.DonHangService;
import com.hoangtuan.coffee_management.service.MonService;
import com.hoangtuan.coffee_management.service.NhomMonService;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/don-hang")
public class DonHangController {

    private final DonHangService donHangService;
    private final MonService monService;
    private final NhomMonService nhomMonService;

    public DonHangController(DonHangService donHangService, MonService monService, NhomMonService nhomMonService) {
        this.donHangService = donHangService;
        this.monService = monService;
        this.nhomMonService = nhomMonService;
    }

    @GetMapping({"", "/", "/index"})
    public String index(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String loaiDonHang,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            Model model
    ) {
        model.addAttribute("danhSachDonHang", donHangService.findAll(keyword, trangThai, loaiDonHang, tuNgay, denNgay));
        model.addAttribute("keyword", keyword);
        model.addAttribute("trangThaiLoc", trangThai);
        model.addAttribute("loaiDonHangLoc", loaiDonHang);
        model.addAttribute("tuNgay", tuNgay);
        model.addAttribute("denNgay", denNgay);
        return "donhang/index";
    }

    @GetMapping("/{maDonHang}")
    public String detail(@PathVariable String maDonHang, Model model) {
        DonHang donHang = donHangService.findById(maDonHang);
        model.addAttribute("donHang", donHang);
        model.addAttribute("chiTietDonHangs", donHangService.findChiTietByDonHang(maDonHang));
        model.addAttribute("canSua", donHangService.kiemTraCoTheSua(donHang));
        return "donhang/detail";
    }

    @GetMapping("/sua/{maDonHang}")
    public String edit(
            @PathVariable String maDonHang,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String maNhomMon,
            Model model
    ) {
        DonHang donHang = donHangService.findById(maDonHang);
        model.addAttribute("donHang", donHang);
        model.addAttribute("chiTietDonHangs", donHangService.findChiTietByDonHang(maDonHang));
        model.addAttribute("canSua", donHangService.kiemTraCoTheSua(donHang));
        model.addAttribute("danhSachMon", monService.searchAndFilter(new MonSearchDTO(keyword, maNhomMon, "true"), true));
        model.addAttribute("danhSachNhomMon", nhomMonService.findAll());
        model.addAttribute("keyword", keyword);
        model.addAttribute("maNhomMonLoc", maNhomMon);
        return "donhang/form-sua";
    }

    @PostMapping("/them-mon/{maDonHang}")
    public String themMon(
            @PathVariable String maDonHang,
            @RequestParam String maMon,
            @RequestParam(defaultValue = "1") Integer soLuong,
            RedirectAttributes redirectAttributes
    ) {
        try {
            donHangService.themMon(maDonHang, maMon, soLuong);
            redirectAttributes.addFlashAttribute("successMessage", "Da them mon vao don hang.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/don-hang/sua/" + maDonHang;
    }

    @PostMapping("/cap-nhat-so-luong/{maCTDH}")
    public String capNhatSoLuong(
            @PathVariable String maCTDH,
            @RequestParam Integer soLuong,
            RedirectAttributes redirectAttributes
    ) {
        String maDonHang;
        try {
            maDonHang = donHangService.findMaDonHangByChiTiet(maCTDH);
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/don-hang";
        }
        try {
            donHangService.capNhatSoLuong(maCTDH, soLuong);
            redirectAttributes.addFlashAttribute("successMessage", "Da cap nhat so luong.");
            return "redirect:/don-hang/sua/" + maDonHang;
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/don-hang/sua/" + maDonHang;
        }
    }

    @PostMapping("/xoa-mon/{maCTDH}")
    public String xoaMon(@PathVariable String maCTDH, RedirectAttributes redirectAttributes) {
        String maDonHang;
        try {
            maDonHang = donHangService.findMaDonHangByChiTiet(maCTDH);
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/don-hang";
        }
        try {
            donHangService.xoaMon(maCTDH);
            redirectAttributes.addFlashAttribute("successMessage", "Da xoa mon khoi don hang.");
            return "redirect:/don-hang/sua/" + maDonHang;
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/don-hang/sua/" + maDonHang;
        }
    }

    @PostMapping("/cap-nhat-trang-thai/{maDonHang}")
    public String capNhatTrangThai(
            @PathVariable String maDonHang,
            @RequestParam String trangThai,
            RedirectAttributes redirectAttributes
    ) {
        try {
            donHangService.capNhatTrangThai(maDonHang, trangThai);
            redirectAttributes.addFlashAttribute("successMessage", "Da cap nhat trang thai don hang.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/don-hang/sua/" + maDonHang;
    }

    @PostMapping("/huy/{maDonHang}")
    public String huy(@PathVariable String maDonHang, RedirectAttributes redirectAttributes) {
        try {
            donHangService.huyDon(maDonHang);
            redirectAttributes.addFlashAttribute("successMessage", "Da huy don hang.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/don-hang/" + maDonHang;
    }
}
