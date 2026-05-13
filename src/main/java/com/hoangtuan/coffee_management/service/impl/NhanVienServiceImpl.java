package com.hoangtuan.coffee_management.service.impl;

import com.hoangtuan.coffee_management.dto.NhanVienFormDTO;
import com.hoangtuan.coffee_management.entity.NhanVien;
import com.hoangtuan.coffee_management.repository.NhanVienRepository;
import com.hoangtuan.coffee_management.repository.TaiKhoanRepository;
import com.hoangtuan.coffee_management.service.NhanVienService;
import com.hoangtuan.coffee_management.util.CodeGeneratorUtil;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class NhanVienServiceImpl implements NhanVienService {

    private final NhanVienRepository nhanVienRepository;
    private final TaiKhoanRepository taiKhoanRepository;

    public NhanVienServiceImpl(NhanVienRepository nhanVienRepository, TaiKhoanRepository taiKhoanRepository) {
        this.nhanVienRepository = nhanVienRepository;
        this.taiKhoanRepository = taiKhoanRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NhanVien> getDanhSach(String keyword, String trangThai) {
        List<NhanVien> nhanViens;
        String search = normalize(keyword);

        if (StringUtils.hasText(search)) {
            nhanViens = nhanVienRepository.findByMaNhanVienContainingIgnoreCaseOrHoTenContainingIgnoreCaseOrSoDienThoaiContainingIgnoreCase(
                    search, search, search
            );
        } else {
            nhanViens = nhanVienRepository.findAll();
        }

        Boolean filterTrangThai = parseTrangThai(trangThai);
        return nhanViens.stream()
                .filter(nhanVien -> filterTrangThai == null || Objects.equals(nhanVien.getTrangThai(), filterTrangThai))
                .sorted((left, right) -> safeString(left.getMaNhanVien()).compareToIgnoreCase(safeString(right.getMaNhanVien())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NhanVien> getNhanVienChuaCoTaiKhoan() {
        return nhanVienRepository.findAll().stream()
                .filter(nhanVien -> nhanVien.getTaiKhoan() == null)
                .sorted((left, right) -> safeString(left.getMaNhanVien()).compareToIgnoreCase(safeString(right.getMaNhanVien())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public NhanVienFormDTO getFormThem() {
        String maNhanVien = CodeGeneratorUtil.generateNextCode(
                "NV",
                nhanVienRepository.findAll().stream().map(NhanVien::getMaNhanVien).collect(Collectors.toList()),
                2
        );
        return new NhanVienFormDTO(maNhanVien, "", "", "", "Nhân viên bán hàng", Boolean.TRUE);
    }

    @Override
    @Transactional(readOnly = true)
    public NhanVienFormDTO getFormSua(String maNhanVien) {
        NhanVien nhanVien = findNhanVien(maNhanVien);
        return new NhanVienFormDTO(
                nhanVien.getMaNhanVien(),
                nhanVien.getHoTen(),
                nhanVien.getSoDienThoai(),
                nhanVien.getDiaChi(),
                nhanVien.getChucVu(),
                nhanVien.getTrangThai()
        );
    }

    @Override
    @Transactional
    public void taoNhanVien(NhanVienFormDTO dto) {
        validateNhanVien(dto);

        String maNhanVien = StringUtils.hasText(dto.getMaNhanVien())
                ? dto.getMaNhanVien().trim()
                : CodeGeneratorUtil.generateNextCode(
                "NV",
                nhanVienRepository.findAll().stream().map(NhanVien::getMaNhanVien).collect(Collectors.toList()),
                2
        );

        if (nhanVienRepository.existsById(maNhanVien)) {
            throw new IllegalArgumentException("Mã nhân viên đã tồn tại.");
        }

        NhanVien nhanVien = new NhanVien();
        nhanVien.setMaNhanVien(maNhanVien);
        nhanVien.setHoTen(normalize(dto.getHoTen()));
        nhanVien.setSoDienThoai(normalize(dto.getSoDienThoai()));
        nhanVien.setDiaChi(normalize(dto.getDiaChi()));
        nhanVien.setChucVu(normalize(dto.getChucVu()));
        nhanVien.setTrangThai(dto.getTrangThai() == null || dto.getTrangThai());

        nhanVienRepository.save(nhanVien);
    }

    @Override
    @Transactional
    public void capNhatNhanVien(String maNhanVien, NhanVienFormDTO dto) {
        validateNhanVien(dto);
        NhanVien nhanVien = findNhanVien(maNhanVien);

        nhanVien.setHoTen(normalize(dto.getHoTen()));
        nhanVien.setSoDienThoai(normalize(dto.getSoDienThoai()));
        nhanVien.setDiaChi(normalize(dto.getDiaChi()));
        nhanVien.setChucVu(normalize(dto.getChucVu()));

        nhanVienRepository.save(nhanVien);
    }

    @Override
    @Transactional
    public void ngungNhanVien(String maNhanVien) {
        NhanVien nhanVien = findNhanVien(maNhanVien);
        nhanVien.setTrangThai(Boolean.FALSE);
        nhanVienRepository.save(nhanVien);
    }

    @Override
    @Transactional
    public void kichHoatNhanVien(String maNhanVien) {
        NhanVien nhanVien = findNhanVien(maNhanVien);
        nhanVien.setTrangThai(Boolean.TRUE);
        nhanVienRepository.save(nhanVien);
    }

    private NhanVien findNhanVien(String maNhanVien) {
        return nhanVienRepository.findById(maNhanVien)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên: " + maNhanVien));
    }

    private void validateNhanVien(NhanVienFormDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Dữ liệu nhân viên không hợp lệ.");
        }
        if (!StringUtils.hasText(dto.getHoTen())) {
            throw new IllegalArgumentException("Họ tên không được để trống.");
        }
        if (!StringUtils.hasText(dto.getSoDienThoai())) {
            throw new IllegalArgumentException("Số điện thoại không được để trống.");
        }
        if (!StringUtils.hasText(dto.getChucVu())) {
            throw new IllegalArgumentException("Chức vụ không được để trống.");
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }

    private Boolean parseTrangThai(String trangThai) {
        if (!StringUtils.hasText(trangThai) || "all".equalsIgnoreCase(trangThai)) {
            return null;
        }
        return Boolean.valueOf(trangThai);
    }
}
