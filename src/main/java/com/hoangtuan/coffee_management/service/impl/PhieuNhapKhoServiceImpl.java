package com.hoangtuan.coffee_management.service.impl;

import com.hoangtuan.coffee_management.dto.ChiTietPhieuNhapFormDTO;
import com.hoangtuan.coffee_management.dto.ChiTietPhieuNhapViewDTO;
import com.hoangtuan.coffee_management.dto.PhieuNhapKhoDetailDTO;
import com.hoangtuan.coffee_management.dto.PhieuNhapKhoFormDTO;
import com.hoangtuan.coffee_management.dto.PhieuNhapKhoListDTO;
import com.hoangtuan.coffee_management.entity.ChiTietPhieuNhap;
import com.hoangtuan.coffee_management.entity.NguyenLieu;
import com.hoangtuan.coffee_management.entity.NhanVien;
import com.hoangtuan.coffee_management.entity.PhieuNhapKho;
import com.hoangtuan.coffee_management.entity.TaiKhoan;
import com.hoangtuan.coffee_management.repository.ChiTietPhieuNhapRepository;
import com.hoangtuan.coffee_management.repository.NguyenLieuRepository;
import com.hoangtuan.coffee_management.repository.NhanVienRepository;
import com.hoangtuan.coffee_management.repository.PhieuNhapKhoRepository;
import com.hoangtuan.coffee_management.repository.TaiKhoanRepository;
import com.hoangtuan.coffee_management.service.NguyenLieuService;
import com.hoangtuan.coffee_management.service.PhieuNhapKhoService;
import com.hoangtuan.coffee_management.util.CodeGeneratorUtil;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class PhieuNhapKhoServiceImpl implements PhieuNhapKhoService {

    private final PhieuNhapKhoRepository phieuNhapKhoRepository;
    private final ChiTietPhieuNhapRepository chiTietPhieuNhapRepository;
    private final NguyenLieuRepository nguyenLieuRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final NhanVienRepository nhanVienRepository;
    private final NguyenLieuService nguyenLieuService;

    public PhieuNhapKhoServiceImpl(
            PhieuNhapKhoRepository phieuNhapKhoRepository,
            ChiTietPhieuNhapRepository chiTietPhieuNhapRepository,
            NguyenLieuRepository nguyenLieuRepository,
            TaiKhoanRepository taiKhoanRepository,
            NhanVienRepository nhanVienRepository,
            NguyenLieuService nguyenLieuService
    ) {
        this.phieuNhapKhoRepository = phieuNhapKhoRepository;
        this.chiTietPhieuNhapRepository = chiTietPhieuNhapRepository;
        this.nguyenLieuRepository = nguyenLieuRepository;
        this.taiKhoanRepository = taiKhoanRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.nguyenLieuService = nguyenLieuService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhieuNhapKhoListDTO> findAll() {
        return phieuNhapKhoRepository.findAll().stream()
                .sorted(Comparator.comparing(PhieuNhapKho::getNgayNhap).reversed())
                .map(this::toListDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhieuNhapKhoListDTO> search(String keyword) {
        String search = normalize(keyword);
        if (!StringUtils.hasText(search)) {
            return findAll();
        }

        return phieuNhapKhoRepository.findAll().stream()
                .filter(phieu -> safeString(phieu.getMaPhieuNhap()).toLowerCase().contains(search.toLowerCase())
                        || safeString(phieu.getGhiChu()).toLowerCase().contains(search.toLowerCase())
                        || (phieu.getNhanVien() != null && safeString(phieu.getNhanVien().getHoTen()).toLowerCase().contains(search.toLowerCase())))
                .sorted(Comparator.comparing(PhieuNhapKho::getNgayNhap).reversed())
                .map(this::toListDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PhieuNhapKho findById(String maPhieuNhap) {
        if (!StringUtils.hasText(maPhieuNhap)) {
            throw new IllegalArgumentException("Vui long chon phieu nhap.");
        }
        return phieuNhapKhoRepository.findById(maPhieuNhap.trim())
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay phieu nhap kho."));
    }

    @Override
    @Transactional(readOnly = true)
    public PhieuNhapKhoDetailDTO getChiTiet(String maPhieuNhap) {
        PhieuNhapKho phieuNhapKho = findById(maPhieuNhap);
        List<ChiTietPhieuNhapViewDTO> chiTiet = chiTietPhieuNhapRepository.findByPhieuNhapKho_MaPhieuNhap(maPhieuNhap).stream()
                .sorted(Comparator.comparing(ChiTietPhieuNhap::getMaCTPN))
                .map(item -> new ChiTietPhieuNhapViewDTO(
                        item.getMaCTPN(),
                        item.getNguyenLieu() != null ? item.getNguyenLieu().getMaNguyenLieu() : "",
                        item.getNguyenLieu() != null ? item.getNguyenLieu().getTenNguyenLieu() : "",
                        item.getSoLuongNhap(),
                        item.getDonGiaNhap(),
                        tinhThanhTien(item.getSoLuongNhap(), item.getDonGiaNhap())
                ))
                .collect(Collectors.toList());
        BigDecimal tongTien = chiTiet.stream()
                .map(item -> item.getThanhTien() == null ? BigDecimal.ZERO : item.getThanhTien())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new PhieuNhapKhoDetailDTO(
                phieuNhapKho.getMaPhieuNhap(),
                phieuNhapKho.getNgayNhap(),
                phieuNhapKho.getGhiChu(),
                phieuNhapKho.getNhanVien() != null ? phieuNhapKho.getNhanVien().getHoTen() : "",
                chiTiet,
                tongTien
        );
    }

    @Override
    @Transactional
    public void createPhieuNhap(PhieuNhapKhoFormDTO dto, String tenDangNhap) {
        validate(dto);
        if (dto.getDanhSachChiTiet() == null || dto.getDanhSachChiTiet().isEmpty()) {
            throw new IllegalArgumentException("Vui long them it nhat mot dong nguyen lieu.");
        }

        TaiKhoan taiKhoan = taiKhoanRepository.findByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay tai khoan dang dang nhap."));
        NhanVien nhanVien = taiKhoan.getNhanVien();
        if (nhanVien == null) {
            throw new IllegalArgumentException("Tai khoan chua gan voi nhan vien.");
        }

        List<ChiTietPhieuNhapFormDTO> validRows = normalizeAndValidateRows(dto.getDanhSachChiTiet());
        String maPhieuNhap = generateNextMaPhieuNhap();
        PhieuNhapKho phieuNhapKho = new PhieuNhapKho();
        phieuNhapKho.setMaPhieuNhap(maPhieuNhap);
        phieuNhapKho.setNgayNhap(LocalDateTime.now());
        phieuNhapKho.setGhiChu(normalize(dto.getGhiChu()));
        phieuNhapKho.setNhanVien(nhanVien);
        phieuNhapKhoRepository.save(phieuNhapKho);

        List<String> existingCtpnCodes = chiTietPhieuNhapRepository.findAll().stream()
                .map(ChiTietPhieuNhap::getMaCTPN)
                .collect(Collectors.toCollection(ArrayList::new));

        Set<String> usedMaNguyenLieu = new HashSet<>();
        for (ChiTietPhieuNhapFormDTO row : validRows) {
            if (!usedMaNguyenLieu.add(row.getMaNguyenLieu())) {
                throw new IllegalArgumentException("Khong duoc nhap trung nguyen lieu trong mot phieu.");
            }

            NguyenLieu nguyenLieu = nguyenLieuRepository.findById(row.getMaNguyenLieu())
                    .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nguyen lieu: " + row.getMaNguyenLieu()));
            String maCTPN = generateNextCodeWithPool("CTPN", existingCtpnCodes, 3);
            existingCtpnCodes.add(maCTPN);

            ChiTietPhieuNhap chiTiet = new ChiTietPhieuNhap();
            chiTiet.setMaCTPN(maCTPN);
            chiTiet.setSoLuongNhap(row.getSoLuongNhap());
            chiTiet.setDonGiaNhap(row.getDonGiaNhap());
            chiTiet.setPhieuNhapKho(phieuNhapKho);
            chiTiet.setNguyenLieu(nguyenLieu);
            chiTietPhieuNhapRepository.save(chiTiet);

            nguyenLieuService.tangTonKho(nguyenLieu.getMaNguyenLieu(), row.getSoLuongNhap());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String generateNextMaPhieuNhap() {
        return CodeGeneratorUtil.generateNextCode(
                "PN",
                phieuNhapKhoRepository.findAll().stream().map(PhieuNhapKho::getMaPhieuNhap).collect(Collectors.toList()),
                3
        );
    }

    @Override
    @Transactional(readOnly = true)
    public String generateNextMaCTPN() {
        return CodeGeneratorUtil.generateNextCode(
                "CTPN",
                chiTietPhieuNhapRepository.findAll().stream().map(ChiTietPhieuNhap::getMaCTPN).collect(Collectors.toList()),
                3
        );
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal tinhTongTien(String maPhieuNhap) {
        return getChiTiet(maPhieuNhap).getTongTien();
    }

    private void validate(PhieuNhapKhoFormDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Du lieu phieu nhap kho khong hop le.");
        }
    }

    private List<ChiTietPhieuNhapFormDTO> normalizeAndValidateRows(List<ChiTietPhieuNhapFormDTO> rows) {
        Map<String, ChiTietPhieuNhapFormDTO> grouped = new HashMap<>();
        boolean hasAnyNonEmptyRow = false;
        for (ChiTietPhieuNhapFormDTO row : rows) {
            if (row == null) {
                continue;
            }
            String maNguyenLieu = normalize(row.getMaNguyenLieu());
            Integer soLuongNhap = row.getSoLuongNhap();
            BigDecimal donGiaNhap = row.getDonGiaNhap();
            if (!StringUtils.hasText(maNguyenLieu)) {
                if (soLuongNhap != null || donGiaNhap != null) {
                    throw new IllegalArgumentException("Vui long chon nguyen lieu cho tung dong.");
                }
                continue;
            }
            hasAnyNonEmptyRow = true;
            if (soLuongNhap == null || soLuongNhap <= 0) {
                throw new IllegalArgumentException("So luong nhap phai lon hon 0.");
            }
            if (donGiaNhap == null || donGiaNhap.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Don gia nhap khong duoc am.");
            }
            ChiTietPhieuNhapFormDTO existing = grouped.get(maNguyenLieu);
            if (existing == null) {
                grouped.put(maNguyenLieu, new ChiTietPhieuNhapFormDTO(maNguyenLieu, soLuongNhap, donGiaNhap));
            } else {
                if (existing.getDonGiaNhap().compareTo(donGiaNhap) != 0) {
                    throw new IllegalArgumentException("Cac dong trung nguyen lieu phai co cung don gia neu muon gop lai.");
                }
                existing.setSoLuongNhap(existing.getSoLuongNhap() + soLuongNhap);
            }
        }
        if (!hasAnyNonEmptyRow || grouped.isEmpty()) {
            throw new IllegalArgumentException("Phieu nhap phai co it nhat mot dong nguyen lieu hop le.");
        }
        return new ArrayList<>(grouped.values());
    }

    private BigDecimal tinhThanhTien(Integer soLuongNhap, BigDecimal donGiaNhap) {
        if (soLuongNhap == null || donGiaNhap == null) {
            return BigDecimal.ZERO;
        }
        return donGiaNhap.multiply(BigDecimal.valueOf(soLuongNhap));
    }

    private PhieuNhapKhoListDTO toListDTO(PhieuNhapKho phieuNhapKho) {
        List<ChiTietPhieuNhap> chiTiet = chiTietPhieuNhapRepository.findByPhieuNhapKho_MaPhieuNhap(phieuNhapKho.getMaPhieuNhap());
        BigDecimal tongTien = chiTiet.stream()
                .map(item -> tinhThanhTien(item.getSoLuongNhap(), item.getDonGiaNhap()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long tongSoNguyenLieu = chiTiet.size();
        return new PhieuNhapKhoListDTO(
                phieuNhapKho.getMaPhieuNhap(),
                phieuNhapKho.getNgayNhap(),
                phieuNhapKho.getNhanVien() != null ? phieuNhapKho.getNhanVien().getHoTen() : "",
                phieuNhapKho.getGhiChu(),
                tongSoNguyenLieu,
                tongTien
        );
    }

    private String generateNextCodeWithPool(String prefix, List<String> existingCodes, int digits) {
        String next = CodeGeneratorUtil.generateNextCode(prefix, existingCodes, digits);
        return next;
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }
}
