package com.hoangtuan.coffee_management.service.impl;

import com.hoangtuan.coffee_management.dto.ChiTietPhieuXuatFormDTO;
import com.hoangtuan.coffee_management.dto.ChiTietPhieuXuatViewDTO;
import com.hoangtuan.coffee_management.dto.PhieuXuatKhoDetailDTO;
import com.hoangtuan.coffee_management.dto.PhieuXuatKhoFormDTO;
import com.hoangtuan.coffee_management.dto.PhieuXuatKhoListDTO;
import com.hoangtuan.coffee_management.entity.ChiTietPhieuXuat;
import com.hoangtuan.coffee_management.entity.NguyenLieu;
import com.hoangtuan.coffee_management.entity.NhanVien;
import com.hoangtuan.coffee_management.entity.PhieuXuatKho;
import com.hoangtuan.coffee_management.entity.TaiKhoan;
import com.hoangtuan.coffee_management.repository.ChiTietPhieuXuatRepository;
import com.hoangtuan.coffee_management.repository.NguyenLieuRepository;
import com.hoangtuan.coffee_management.repository.PhieuXuatKhoRepository;
import com.hoangtuan.coffee_management.repository.TaiKhoanRepository;
import com.hoangtuan.coffee_management.service.NguyenLieuService;
import com.hoangtuan.coffee_management.service.PhieuXuatKhoService;
import com.hoangtuan.coffee_management.util.CodeGeneratorUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class PhieuXuatKhoServiceImpl implements PhieuXuatKhoService {

    private final PhieuXuatKhoRepository phieuXuatKhoRepository;
    private final ChiTietPhieuXuatRepository chiTietPhieuXuatRepository;
    private final NguyenLieuRepository nguyenLieuRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final NguyenLieuService nguyenLieuService;

    public PhieuXuatKhoServiceImpl(
            PhieuXuatKhoRepository phieuXuatKhoRepository,
            ChiTietPhieuXuatRepository chiTietPhieuXuatRepository,
            NguyenLieuRepository nguyenLieuRepository,
            TaiKhoanRepository taiKhoanRepository,
            NguyenLieuService nguyenLieuService
    ) {
        this.phieuXuatKhoRepository = phieuXuatKhoRepository;
        this.chiTietPhieuXuatRepository = chiTietPhieuXuatRepository;
        this.nguyenLieuRepository = nguyenLieuRepository;
        this.taiKhoanRepository = taiKhoanRepository;
        this.nguyenLieuService = nguyenLieuService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhieuXuatKhoListDTO> findAll() {
        return phieuXuatKhoRepository.findAll().stream()
                .sorted(Comparator.comparing(PhieuXuatKho::getNgayXuat).reversed())
                .map(this::toListDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhieuXuatKhoListDTO> search(String keyword) {
        String search = normalize(keyword);
        if (!StringUtils.hasText(search)) {
            return findAll();
        }
        return phieuXuatKhoRepository.findAll().stream()
                .filter(phieu -> safeString(phieu.getMaPhieuXuat()).toLowerCase().contains(search.toLowerCase())
                        || safeString(phieu.getLyDoXuat()).toLowerCase().contains(search.toLowerCase())
                        || (phieu.getNhanVien() != null && safeString(phieu.getNhanVien().getHoTen()).toLowerCase().contains(search.toLowerCase())))
                .sorted(Comparator.comparing(PhieuXuatKho::getNgayXuat).reversed())
                .map(this::toListDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PhieuXuatKho findById(String maPhieuXuat) {
        if (!StringUtils.hasText(maPhieuXuat)) {
            throw new IllegalArgumentException("Vui long chon phieu xuat.");
        }
        return phieuXuatKhoRepository.findById(maPhieuXuat.trim())
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay phieu xuat kho."));
    }

    @Override
    @Transactional(readOnly = true)
    public PhieuXuatKhoDetailDTO getChiTiet(String maPhieuXuat) {
        PhieuXuatKho phieuXuatKho = findById(maPhieuXuat);
        List<ChiTietPhieuXuatViewDTO> danhSachChiTiet = chiTietPhieuXuatRepository.findByPhieuXuatKho_MaPhieuXuat(maPhieuXuat).stream()
                .sorted(Comparator.comparing(ChiTietPhieuXuat::getMaCTPX))
                .map(item -> new ChiTietPhieuXuatViewDTO(
                        item.getMaCTPX(),
                        item.getNguyenLieu() != null ? item.getNguyenLieu().getMaNguyenLieu() : "",
                        item.getNguyenLieu() != null ? item.getNguyenLieu().getTenNguyenLieu() : "",
                        item.getSoLuongXuat(),
                        item.getNguyenLieu() != null ? item.getNguyenLieu().getDonViTinh() : ""
                ))
                .collect(Collectors.toList());
        return new PhieuXuatKhoDetailDTO(
                phieuXuatKho.getMaPhieuXuat(),
                phieuXuatKho.getNgayXuat(),
                phieuXuatKho.getLyDoXuat(),
                phieuXuatKho.getNhanVien() != null ? phieuXuatKho.getNhanVien().getHoTen() : "",
                danhSachChiTiet
        );
    }

    @Override
    @Transactional
    public void createPhieuXuat(PhieuXuatKhoFormDTO dto, String tenDangNhap) {
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

        List<ChiTietPhieuXuatFormDTO> validRows = normalizeAndValidateRows(dto.getDanhSachChiTiet());
        Map<String, Integer> grouped = new HashMap<>();
        for (ChiTietPhieuXuatFormDTO row : validRows) {
            grouped.merge(row.getMaNguyenLieu(), row.getSoLuongXuat(), Integer::sum);
        }

        for (Map.Entry<String, Integer> entry : grouped.entrySet()) {
            if (!nguyenLieuService.kiemTraDuTonKho(entry.getKey(), entry.getValue())) {
                NguyenLieu nguyenLieu = nguyenLieuRepository.findById(entry.getKey())
                        .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nguyen lieu: " + entry.getKey()));
                throw new IllegalArgumentException("Nguyen lieu " + nguyenLieu.getTenNguyenLieu() + " khong du ton kho.");
            }
        }

        String maPhieuXuat = generateNextMaPhieuXuat();
        PhieuXuatKho phieuXuatKho = new PhieuXuatKho();
        phieuXuatKho.setMaPhieuXuat(maPhieuXuat);
        phieuXuatKho.setNgayXuat(LocalDateTime.now());
        phieuXuatKho.setLyDoXuat(normalize(dto.getLyDoXuat()));
        phieuXuatKho.setNhanVien(nhanVien);
        phieuXuatKhoRepository.save(phieuXuatKho);

        List<String> existingCodes = chiTietPhieuXuatRepository.findAll().stream()
                .map(ChiTietPhieuXuat::getMaCTPX)
                .collect(Collectors.toCollection(ArrayList::new));

        for (Map.Entry<String, Integer> entry : grouped.entrySet()) {
            NguyenLieu nguyenLieu = nguyenLieuRepository.findById(entry.getKey())
                    .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nguyen lieu: " + entry.getKey()));

            String maCTPX = CodeGeneratorUtil.generateNextCode("CTPX", existingCodes, 3);
            existingCodes.add(maCTPX);

            ChiTietPhieuXuat chiTiet = new ChiTietPhieuXuat();
            chiTiet.setMaCTPX(maCTPX);
            chiTiet.setSoLuongXuat(entry.getValue());
            chiTiet.setPhieuXuatKho(phieuXuatKho);
            chiTiet.setNguyenLieu(nguyenLieu);
            chiTietPhieuXuatRepository.save(chiTiet);

            nguyenLieuService.giamTonKho(nguyenLieu.getMaNguyenLieu(), entry.getValue());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String generateNextMaPhieuXuat() {
        return CodeGeneratorUtil.generateNextCode(
                "PX",
                phieuXuatKhoRepository.findAll().stream().map(PhieuXuatKho::getMaPhieuXuat).collect(Collectors.toList()),
                3
        );
    }

    @Override
    @Transactional(readOnly = true)
    public String generateNextMaCTPX() {
        return CodeGeneratorUtil.generateNextCode(
                "CTPX",
                chiTietPhieuXuatRepository.findAll().stream().map(ChiTietPhieuXuat::getMaCTPX).collect(Collectors.toList()),
                3
        );
    }

    private void validate(PhieuXuatKhoFormDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Du lieu phieu xuat kho khong hop le.");
        }
        if (!StringUtils.hasText(dto.getLyDoXuat())) {
            throw new IllegalArgumentException("Ly do xuat khong duoc de trong.");
        }
    }

    private List<ChiTietPhieuXuatFormDTO> normalizeAndValidateRows(List<ChiTietPhieuXuatFormDTO> rows) {
        List<ChiTietPhieuXuatFormDTO> result = new ArrayList<>();
        boolean hasAnyValid = false;
        for (ChiTietPhieuXuatFormDTO row : rows) {
            if (row == null) {
                continue;
            }
            String maNguyenLieu = normalize(row.getMaNguyenLieu());
            Integer soLuongXuat = row.getSoLuongXuat();
            if (!StringUtils.hasText(maNguyenLieu)) {
                if (soLuongXuat != null) {
                    throw new IllegalArgumentException("Vui long chon nguyen lieu cho tung dong.");
                }
                continue;
            }
            hasAnyValid = true;
            if (soLuongXuat == null || soLuongXuat <= 0) {
                throw new IllegalArgumentException("So luong xuat phai lon hon 0.");
            }
            result.add(new ChiTietPhieuXuatFormDTO(maNguyenLieu, soLuongXuat));
        }
        if (!hasAnyValid || result.isEmpty()) {
            throw new IllegalArgumentException("Phieu xuat phai co it nhat mot dong nguyen lieu hop le.");
        }
        return result;
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }

    private PhieuXuatKhoListDTO toListDTO(PhieuXuatKho phieuXuatKho) {
        long tongSoNguyenLieu = chiTietPhieuXuatRepository.findByPhieuXuatKho_MaPhieuXuat(phieuXuatKho.getMaPhieuXuat()).size();
        return new PhieuXuatKhoListDTO(
                phieuXuatKho.getMaPhieuXuat(),
                phieuXuatKho.getNgayXuat(),
                phieuXuatKho.getNhanVien() != null ? phieuXuatKho.getNhanVien().getHoTen() : "",
                phieuXuatKho.getLyDoXuat(),
                tongSoNguyenLieu
        );
    }
}
