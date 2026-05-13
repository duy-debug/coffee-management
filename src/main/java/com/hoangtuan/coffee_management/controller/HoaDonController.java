package com.hoangtuan.coffee_management.controller;

import com.hoangtuan.coffee_management.dto.HoaDonThanhToanViewDTO;
import com.hoangtuan.coffee_management.dto.ThanhToanDTO;
import com.hoangtuan.coffee_management.entity.HoaDon;
import com.hoangtuan.coffee_management.service.DonHangService;
import com.hoangtuan.coffee_management.service.HoaDonService;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/hoa-don")
public class HoaDonController {

    private final HoaDonService hoaDonService;
    private final DonHangService donHangService;

    public HoaDonController(HoaDonService hoaDonService, DonHangService donHangService) {
        this.hoaDonService = hoaDonService;
        this.donHangService = donHangService;
    }

    @GetMapping({"", "/", "/index"})
    public String index(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String phuongThucThanhToan,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            Model model
    ) {
        model.addAttribute("danhSachHoaDon", hoaDonService.findAll(keyword, phuongThucThanhToan, tuNgay, denNgay));
        model.addAttribute("danhSachDonHangChoThanhToan", hoaDonService.findDonHangChoThanhToan());
        model.addAttribute("keyword", keyword);
        model.addAttribute("phuongThucThanhToanLoc", phuongThucThanhToan);
        model.addAttribute("tuNgay", tuNgay);
        model.addAttribute("denNgay", denNgay);
        return "hoadon/index";
    }

    @GetMapping("/don-hang-cho-thanh-toan")
    public String donHangChoThanhToan(Model model) {
        model.addAttribute("danhSachDonHang", hoaDonService.findDonHangChoThanhToan());
        return "hoadon/don-hang-cho-thanh-toan";
    }

    @GetMapping("/thanh-toan/{maDonHang}")
    public String thanhToan(
            @PathVariable String maDonHang,
            @RequestParam(required = false) String maKhuyenMai,
            @RequestParam(required = false) String phuongThucThanhToan,
            Model model
    ) {
        HoaDonThanhToanViewDTO viewDTO = hoaDonService.getThanhToanData(maDonHang, maKhuyenMai, phuongThucThanhToan);
        model.addAttribute("thanhToanView", viewDTO);
        model.addAttribute("thanhToanForm", new ThanhToanDTO(viewDTO.getPhuongThucThanhToan(), viewDTO.getMaKhuyenMai()));
        return "hoadon/thanh-toan";
    }

    @PostMapping("/thanh-toan/{maDonHang}")
    public String xuLyThanhToan(
            @PathVariable String maDonHang,
            @ModelAttribute ThanhToanDTO thanhToanDTO,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            HoaDon hoaDon = hoaDonService.thanhToan(maDonHang, thanhToanDTO, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Thanh toan hoa don thanh cong.");
            return "redirect:/hoa-don/" + hoaDon.getMaHoaDon();
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/hoa-don/thanh-toan/" + maDonHang;
        }
    }

    @GetMapping("/{maHoaDon}")
    public String detail(@PathVariable String maHoaDon, Model model) {
        HoaDon hoaDon = hoaDonService.findById(maHoaDon);
        model.addAttribute("hoaDon", hoaDon);
        model.addAttribute("chiTietDonHangs", donHangService.findChiTietByDonHang(hoaDon.getDonHang().getMaDonHang()));
        return "hoadon/detail";
    }

    @GetMapping("/in/{maHoaDon}")
    public String in(@PathVariable String maHoaDon, Model model) {
        HoaDon hoaDon = hoaDonService.findById(maHoaDon);
        model.addAttribute("hoaDon", hoaDon);
        model.addAttribute("chiTietDonHangs", donHangService.findChiTietByDonHang(hoaDon.getDonHang().getMaDonHang()));
        return "hoadon/in";
    }
}
