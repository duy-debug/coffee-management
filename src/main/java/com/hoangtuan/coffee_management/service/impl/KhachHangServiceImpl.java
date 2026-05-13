package com.hoangtuan.coffee_management.service.impl;

import com.hoangtuan.coffee_management.dto.KhachHangDetailDTO;
import com.hoangtuan.coffee_management.dto.KhachHangFormDTO;
import com.hoangtuan.coffee_management.dto.LichSuMuaHangDTO;
import com.hoangtuan.coffee_management.entity.DonHang;
import com.hoangtuan.coffee_management.entity.HoaDon;
import com.hoangtuan.coffee_management.entity.KhachHang;
import com.hoangtuan.coffee_management.repository.DonHangRepository;
import com.hoangtuan.coffee_management.repository.HoaDonRepository;
import com.hoangtuan.coffee_management.repository.KhachHangRepository;
import com.hoangtuan.coffee_management.service.KhachHangService;
import com.hoangtuan.coffee_management.util.CodeGeneratorUtil;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class KhachHangServiceImpl implements KhachHangService {

    private final KhachHangRepository khachHangRepository;
    private final DonHangRepository donHangRepository;
    private final HoaDonRepository hoaDonRepository;

    public KhachHangServiceImpl(
            KhachHangRepository khachHangRepository,
            DonHangRepository donHangRepository,
            HoaDonRepository hoaDonRepository
    ) {
        this.khachHangRepository = khachHangRepository;
        this.donHangRepository = donHangRepository;
        this.hoaDonRepository = hoaDonRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<KhachHang> findAll() {
        return khachHangRepository.findAll().stream()
                .sorted(Comparator.comparing(k -> safeString(k.getMaKhachHang()), String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<KhachHang> search(String keyword) {
        String search = normalize(keyword);
        if (!StringUtils.hasText(search)) {
            return findAll();
        }

        return khachHangRepository.findAll().stream()
                .filter(khachHang -> safeString(khachHang.getMaKhachHang()).toLowerCase().contains(search.toLowerCase())
                        || safeString(khachHang.getHoTen()).toLowerCase().contains(search.toLowerCase())
                        || safeString(khachHang.getSoDienThoai()).toLowerCase().contains(search.toLowerCase()))
                .sorted(Comparator.comparing(k -> safeString(k.getMaKhachHang()), String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public KhachHang findById(String maKhachHang) {
        return khachHangRepository.findById(maKhachHang)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay khach hang: " + maKhachHang));
    }

    @Override
    @Transactional(readOnly = true)
    public KhachHangFormDTO getFormThem() {
        return new KhachHangFormDTO(generateNextMaKhachHang(), "", "", "");
    }

    @Override
    @Transactional(readOnly = true)
    public KhachHangFormDTO getFormSua(String maKhachHang) {
        KhachHang khachHang = findById(maKhachHang);
        return new KhachHangFormDTO(
                khachHang.getMaKhachHang(),
                khachHang.getHoTen(),
                khachHang.getSoDienThoai(),
                khachHang.getGhiChu()
        );
    }

    @Override
    @Transactional
    public void save(KhachHangFormDTO dto) {
        validate(dto);
        String maKhachHang = StringUtils.hasText(dto.getMaKhachHang())
                ? dto.getMaKhachHang().trim()
                : generateNextMaKhachHang();
        if (khachHangRepository.existsById(maKhachHang)) {
            throw new IllegalArgumentException("Ma khach hang da ton tai.");
        }
        String soDienThoai = normalize(dto.getSoDienThoai());
        if (StringUtils.hasText(soDienThoai) && khachHangRepository.findBySoDienThoai(soDienThoai).isPresent()) {
            throw new IllegalArgumentException("So dien thoai da ton tai.");
        }
        KhachHang khachHang = new KhachHang(maKhachHang, normalize(dto.getHoTen()), soDienThoai, normalize(dto.getGhiChu()));
        khachHangRepository.save(khachHang);
    }

    @Override
    @Transactional
    public void update(String maKhachHang, KhachHangFormDTO dto) {
        validate(dto);
        KhachHang khachHang = findById(maKhachHang);
        String soDienThoai = normalize(dto.getSoDienThoai());
        if (StringUtils.hasText(soDienThoai)
                && khachHangRepository.findBySoDienThoai(soDienThoai).filter(existing -> !Objects.equals(existing.getMaKhachHang(), khachHang.getMaKhachHang())).isPresent()) {
            throw new IllegalArgumentException("So dien thoai da ton tai.");
        }
        khachHang.setHoTen(normalize(dto.getHoTen()));
        khachHang.setSoDienThoai(soDienThoai);
        khachHang.setGhiChu(normalize(dto.getGhiChu()));
        khachHangRepository.save(khachHang);
    }

    @Override
    @Transactional(readOnly = true)
    public KhachHangDetailDTO getChiTiet(String maKhachHang) {
        KhachHang khachHang = findById(maKhachHang);
        List<LichSuMuaHangDTO> lichSu = getLichSuMuaHang(maKhachHang);
        long tongSoDon = lichSu.size();
        long tongSoHoaDon = lichSu.stream().filter(item -> StringUtils.hasText(item.getMaHoaDon())).count();
        BigDecimal tongTien = lichSu.stream()
                .filter(item -> item.getSoTienPhaiTra() != null)
                .map(LichSuMuaHangDTO::getSoTienPhaiTra)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new KhachHangDetailDTO(khachHang, tongSoDon, tongSoHoaDon, tongTien, lichSu);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LichSuMuaHangDTO> getLichSuMuaHang(String maKhachHang) {
        List<DonHang> donHangs = donHangRepository.findAll().stream()
                .filter(donHang -> donHang.getKhachHang() != null && Objects.equals(donHang.getKhachHang().getMaKhachHang(), maKhachHang))
                .sorted(Comparator.comparing(DonHang::getNgayDat).reversed())
                .collect(Collectors.toList());

        return donHangs.stream()
                .map(donHang -> {
                    HoaDon hoaDon = hoaDonRepository.findByDonHang_MaDonHang(donHang.getMaDonHang()).orElse(null);
                    return new LichSuMuaHangDTO(
                            donHang.getMaDonHang(),
                            donHang.getNgayDat(),
                            donHang.getLoaiDonHang(),
                            donHang.getTrangThai(),
                            donHang.getTongTien(),
                            hoaDon != null ? hoaDon.getMaHoaDon() : "",
                            hoaDon != null ? hoaDon.getSoTienPhaiTra() : null,
                            hoaDon != null ? hoaDon.getNgayThanhToan() : null,
                            hoaDon != null ? hoaDon.getPhuongThucThanhToan() : ""
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public String generateNextMaKhachHang() {
        return CodeGeneratorUtil.generateNextCode(
                "KH",
                khachHangRepository.findAll().stream().map(KhachHang::getMaKhachHang).collect(Collectors.toList()),
                2
        );
    }

    private void validate(KhachHangFormDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Du lieu khach hang khong hop le.");
        }
        if (!StringUtils.hasText(dto.getHoTen())) {
            throw new IllegalArgumentException("Ho ten khach hang khong duoc de trong.");
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }
}
