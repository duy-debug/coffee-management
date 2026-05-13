package com.hoangtuan.coffee_management.controller;

import com.hoangtuan.coffee_management.dto.BanHangPageDTO;
import com.hoangtuan.coffee_management.dto.BanHangRequestDTO;
import com.hoangtuan.coffee_management.service.BanHangService;
import com.hoangtuan.coffee_management.service.ChiTietDonHangService;
import com.hoangtuan.coffee_management.service.DonHangService;
import jakarta.servlet.http.HttpSession;
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
@RequestMapping("/ban-hang")
public class BanHangController {

    private final BanHangService banHangService;
    private final DonHangService donHangService;
    private final ChiTietDonHangService chiTietDonHangService;

    public BanHangController(
            BanHangService banHangService,
            DonHangService donHangService,
            ChiTietDonHangService chiTietDonHangService
    ) {
        this.banHangService = banHangService;
        this.donHangService = donHangService;
        this.chiTietDonHangService = chiTietDonHangService;
    }

    @GetMapping({"", "/", "/index"})
    public String index(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String maNhomMon,
            @RequestParam(required = false) String loaiDonHang,
            @RequestParam(required = false) String maBan,
            @RequestParam(required = false) String maKhachHang,
            HttpSession session,
            Model model
    ) {
        BanHangPageDTO pageData = banHangService.getBanHangPageData(keyword, maNhomMon, loaiDonHang, maBan, maKhachHang, session);
        model.addAttribute("pageData", pageData);
        model.addAttribute("banHangRequest", new BanHangRequestDTO(pageData.getLoaiDonHang(), pageData.getMaBan(), pageData.getMaKhachHang()));
        return "banhang/index";
    }

    @PostMapping("/them-mon")
    public String themMon(
            @RequestParam String maMon,
            @RequestParam(defaultValue = "1") Integer soLuong,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        try {
            banHangService.themMonVaoGio(maMon, soLuong, session);
            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm món vào giỏ hàng.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ban-hang";
    }

    @PostMapping("/cap-nhat-so-luong")
    public String capNhatSoLuong(
            @RequestParam String maMon,
            @RequestParam Integer soLuong,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        try {
            banHangService.capNhatSoLuong(maMon, soLuong, session);
            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật số lượng.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ban-hang";
    }

    @PostMapping("/xoa-mon")
    public String xoaMon(
            @RequestParam String maMon,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        try {
            banHangService.xoaMon(maMon, session);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa món khỏi giỏ hàng.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ban-hang";
    }

    @PostMapping("/tao-don")
    public String taoDon(
            @ModelAttribute("banHangRequest") BanHangRequestDTO banHangRequest,
            Authentication authentication,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        try {
            var donHang = banHangService.taoDonHang(banHangRequest, authentication.getName(), session);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo đơn hàng thành công.");
            return "redirect:/ban-hang/don/" + donHang.getMaDonHang();
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/ban-hang";
        }
    }

    @GetMapping("/don/{maDonHang}")
    public String chiTietDon(@PathVariable String maDonHang, Model model) {
        model.addAttribute("donHang", donHangService.findById(maDonHang));
        model.addAttribute("chiTietDonHangs", chiTietDonHangService.findByDonHang(maDonHang));
        return "banhang/chi-tiet-don";
    }

    @PostMapping("/huy-don/{maDonHang}")
    public String huyDon(@PathVariable String maDonHang, RedirectAttributes redirectAttributes) {
        try {
            banHangService.huyDon(maDonHang);
            redirectAttributes.addFlashAttribute("successMessage", "Đã hủy đơn hàng.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/ban-hang/don/" + maDonHang;
    }
}
