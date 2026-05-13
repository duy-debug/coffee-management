package com.hoangtuan.coffee_management.service.impl;

import com.hoangtuan.coffee_management.dto.BanHangItemDTO;
import com.hoangtuan.coffee_management.dto.BanHangPageDTO;
import com.hoangtuan.coffee_management.dto.BanHangRequestDTO;
import com.hoangtuan.coffee_management.dto.MonSearchDTO;
import com.hoangtuan.coffee_management.entity.Ban;
import com.hoangtuan.coffee_management.entity.DonHang;
import com.hoangtuan.coffee_management.entity.KhachHang;
import com.hoangtuan.coffee_management.entity.Mon;
import com.hoangtuan.coffee_management.entity.NhomMon;
import com.hoangtuan.coffee_management.repository.BanRepository;
import com.hoangtuan.coffee_management.repository.KhachHangRepository;
import com.hoangtuan.coffee_management.repository.MonRepository;
import com.hoangtuan.coffee_management.repository.TaiKhoanRepository;
import com.hoangtuan.coffee_management.service.BanHangService;
import com.hoangtuan.coffee_management.service.BanService;
import com.hoangtuan.coffee_management.service.DonHangService;
import com.hoangtuan.coffee_management.service.MonService;
import com.hoangtuan.coffee_management.service.NhomMonService;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class BanHangServiceImpl implements BanHangService {

    private static final String SESSION_CART_KEY = "BAN_HANG_GIO_HANG";

    private final BanService banService;
    private final MonService monService;
    private final NhomMonService nhomMonService;
    private final KhachHangRepository khachHangRepository;
    private final DonHangService donHangService;
    private final BanRepository banRepository;
    private final TaiKhoanRepository taiKhoanRepository;

    public BanHangServiceImpl(
            BanService banService,
            MonService monService,
            NhomMonService nhomMonService,
            KhachHangRepository khachHangRepository,
            DonHangService donHangService,
            BanRepository banRepository,
            TaiKhoanRepository taiKhoanRepository
    ) {
        this.banService = banService;
        this.monService = monService;
        this.nhomMonService = nhomMonService;
        this.khachHangRepository = khachHangRepository;
        this.donHangService = donHangService;
        this.banRepository = banRepository;
        this.taiKhoanRepository = taiKhoanRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public BanHangPageDTO getBanHangPageData(String keyword, String maNhomMon, String loaiDonHang, String maBan, String maKhachHang, HttpSession session) {
        List<BanHangItemDTO> gioHang = getCart(session);
        return new BanHangPageDTO(
                keyword,
                maNhomMon,
                StringUtils.hasText(loaiDonHang) ? loaiDonHang : "Mang đi",
                maBan,
                maKhachHang,
                banService.findBanTrong(),
                nhomMonService.findAll(),
                monService.searchAndFilter(new MonSearchDTO(keyword, maNhomMon, "true"), true),
                khachHangRepository.findAll(),
                gioHang,
                tinhTongTien(gioHang)
        );
    }

    @Override
    @Transactional
    public void themMonVaoGio(String maMon, Integer soLuong, HttpSession session) {
        if (!StringUtils.hasText(maMon)) {
            throw new IllegalArgumentException("Vui lòng chọn món.");
        }
        if (soLuong == null || soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0.");
        }
        Mon mon = monService.findById(maMon);
        if (Boolean.FALSE.equals(mon.getTrangThai())) {
            throw new IllegalArgumentException("Không thể thêm món ngừng bán.");
        }

        List<BanHangItemDTO> gioHang = getCart(session);
        Optional<BanHangItemDTO> existing = gioHang.stream()
                .filter(item -> item.getMaMon().equalsIgnoreCase(maMon))
                .findFirst();
        if (existing.isPresent()) {
            BanHangItemDTO item = existing.get();
            item.setSoLuong(item.getSoLuong() + soLuong);
            item.setThanhTien(item.getDonGia().multiply(BigDecimal.valueOf(item.getSoLuong())));
        } else {
            gioHang.add(new BanHangItemDTO(
                    mon.getMaMon(),
                    mon.getTenMon(),
                    mon.getHinhAnh(),
                    mon.getNhomMon() != null ? mon.getNhomMon().getMaNhomMon() : "",
                    mon.getNhomMon() != null ? mon.getNhomMon().getTenNhomMon() : "",
                    mon.getDonGia(),
                    soLuong,
                    mon.getDonGia().multiply(BigDecimal.valueOf(soLuong))
            ));
        }
        saveCart(session, gioHang);
    }

    @Override
    @Transactional
    public void capNhatSoLuong(String maMon, Integer soLuong, HttpSession session) {
        if (!StringUtils.hasText(maMon)) {
            throw new IllegalArgumentException("Mã món không hợp lệ.");
        }
        if (soLuong == null || soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0.");
        }
        List<BanHangItemDTO> gioHang = getCart(session);
        BanHangItemDTO item = gioHang.stream()
                .filter(it -> it.getMaMon().equalsIgnoreCase(maMon))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy món trong giỏ hàng."));
        item.setSoLuong(soLuong);
        item.setThanhTien(item.getDonGia().multiply(BigDecimal.valueOf(soLuong)));
        saveCart(session, gioHang);
    }

    @Override
    @Transactional
    public void xoaMon(String maMon, HttpSession session) {
        List<BanHangItemDTO> gioHang = getCart(session);
        gioHang.removeIf(item -> item.getMaMon().equalsIgnoreCase(maMon));
        saveCart(session, gioHang);
    }

    @Override
    @Transactional
    public DonHang taoDonHang(BanHangRequestDTO dto, String tenDangNhap, HttpSession session) {
        List<BanHangItemDTO> gioHang = getCart(session);
        DonHang donHang = donHangService.createDonHang(dto, tenDangNhap, gioHang);
        clearCart(session);
        return donHang;
    }

    @Override
    @Transactional
    public void huyDon(String maDonHang) {
        DonHang donHang = donHangService.findById(maDonHang);
        donHangService.updateTrangThai(maDonHang, "Đã hủy");
        if (donHang.getBan() != null) {
            Ban ban = donHang.getBan();
            ban.setTrangThai("Trống");
            banRepository.save(ban);
        }
    }

    private List<BanHangItemDTO> getCart(HttpSession session) {
        @SuppressWarnings("unchecked")
        List<BanHangItemDTO> gioHang = (List<BanHangItemDTO>) session.getAttribute(SESSION_CART_KEY);
        if (gioHang == null) {
            gioHang = new ArrayList<>();
        }
        return gioHang;
    }

    private void saveCart(HttpSession session, List<BanHangItemDTO> gioHang) {
        session.setAttribute(SESSION_CART_KEY, gioHang);
    }

    private void clearCart(HttpSession session) {
        session.removeAttribute(SESSION_CART_KEY);
    }

    private BigDecimal tinhTongTien(List<BanHangItemDTO> gioHang) {
        return gioHang.stream()
                .map(BanHangItemDTO::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
