package com.hoangtuan.coffee_management.service.impl;

import com.hoangtuan.coffee_management.dto.MonFormDTO;
import com.hoangtuan.coffee_management.dto.MonSearchDTO;
import com.hoangtuan.coffee_management.entity.Mon;
import com.hoangtuan.coffee_management.entity.NhomMon;
import com.hoangtuan.coffee_management.repository.MonRepository;
import com.hoangtuan.coffee_management.repository.NhomMonRepository;
import com.hoangtuan.coffee_management.service.MonService;
import com.hoangtuan.coffee_management.util.CodeGeneratorUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MonServiceImpl implements MonService {

    private final MonRepository monRepository;
    private final NhomMonRepository nhomMonRepository;

    public MonServiceImpl(MonRepository monRepository, NhomMonRepository nhomMonRepository) {
        this.monRepository = monRepository;
        this.nhomMonRepository = nhomMonRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Mon> findAll() {
        return monRepository.findAll().stream()
                .sorted((left, right) -> safeString(left.getMaMon()).compareToIgnoreCase(safeString(right.getMaMon())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Mon> findDangBan() {
        return monRepository.findByTrangThai(Boolean.TRUE).stream()
                .sorted((left, right) -> safeString(left.getMaMon()).compareToIgnoreCase(safeString(right.getMaMon())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Mon> searchAndFilter(MonSearchDTO searchDTO, boolean chiXemDangBan) {
        final String keyword = searchDTO != null ? normalize(searchDTO.getKeyword()) : null;
        final String maNhomMon = searchDTO != null ? normalize(searchDTO.getMaNhomMon()) : null;
        final Boolean trangThai;
        if (searchDTO != null && StringUtils.hasText(searchDTO.getTrangThai()) && !"all".equalsIgnoreCase(searchDTO.getTrangThai())) {
            trangThai = Boolean.valueOf(searchDTO.getTrangThai());
        } else {
            trangThai = null;
        }

        List<Mon> mons = monRepository.findAll();
        if (chiXemDangBan) {
            mons = mons.stream().filter(mon -> Boolean.TRUE.equals(mon.getTrangThai())).collect(Collectors.toList());
        }

        return mons.stream()
                .filter(mon -> !StringUtils.hasText(keyword)
                        || safeString(mon.getMaMon()).toLowerCase().contains(keyword.toLowerCase())
                        || safeString(mon.getTenMon()).toLowerCase().contains(keyword.toLowerCase()))
                .filter(mon -> !StringUtils.hasText(maNhomMon)
                        || (mon.getNhomMon() != null && maNhomMon.equalsIgnoreCase(mon.getNhomMon().getMaNhomMon())))
                .filter(mon -> trangThai == null || Objects.equals(mon.getTrangThai(), trangThai))
                .sorted((left, right) -> safeString(left.getMaMon()).compareToIgnoreCase(safeString(right.getMaMon())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Mon findById(String maMon) {
        return monRepository.findById(maMon)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy món: " + maMon));
    }

    @Override
    @Transactional(readOnly = true)
    public MonFormDTO getFormThem() {
        return new MonFormDTO(generateNextMaMon(), "", BigDecimal.ZERO, "", "", Boolean.TRUE, "", "");
    }

    @Override
    @Transactional(readOnly = true)
    public MonFormDTO getFormSua(String maMon) {
        Mon mon = findById(maMon);
        return new MonFormDTO(
                mon.getMaMon(),
                mon.getTenMon(),
                mon.getDonGia(),
                mon.getMoTa(),
                mon.getHinhAnh(),
                mon.getTrangThai(),
                mon.getNhomMon() != null ? mon.getNhomMon().getMaNhomMon() : "",
                mon.getNhomMon() != null ? mon.getNhomMon().getTenNhomMon() : ""
        );
    }

    @Override
    @Transactional
    public void save(MonFormDTO dto, MultipartFile hinhAnhFile) {
        validate(dto);
        String maMon = StringUtils.hasText(dto.getMaMon())
                ? dto.getMaMon().trim()
                : generateNextMaMon();
        if (monRepository.existsById(maMon)) {
            throw new IllegalArgumentException("Mã món đã tồn tại.");
        }
        if (monRepository.existsByTenMonIgnoreCase(normalize(dto.getTenMon()))) {
            throw new IllegalArgumentException("Tên món đã tồn tại.");
        }

        NhomMon nhomMon = findNhomMon(dto.getMaNhomMon());
        Mon mon = new Mon();
        mon.setMaMon(maMon);
        mon.setTenMon(normalize(dto.getTenMon()));
        mon.setDonGia(dto.getDonGia());
        mon.setMoTa(normalize(dto.getMoTa()));
        mon.setHinhAnh(resolveImageFileName(hinhAnhFile, dto.getHinhAnh(), maMon));
        mon.setTrangThai(dto.getTrangThai() == null || dto.getTrangThai());
        mon.setNhomMon(nhomMon);
        monRepository.save(mon);
    }

    @Override
    @Transactional
    public void update(String maMon, MonFormDTO dto, MultipartFile hinhAnhFile) {
        validate(dto);
        Mon mon = findById(maMon);
        String tenMoi = normalize(dto.getTenMon());
        if (monRepository.existsByTenMonIgnoreCaseAndMaMonNot(tenMoi, mon.getMaMon())) {
            throw new IllegalArgumentException("Tên món đã tồn tại.");
        }

        mon.setTenMon(tenMoi);
        mon.setDonGia(dto.getDonGia());
        mon.setMoTa(normalize(dto.getMoTa()));
        mon.setHinhAnh(resolveImageFileName(hinhAnhFile, dto.getHinhAnh(), mon.getMaMon()));
        mon.setNhomMon(findNhomMon(dto.getMaNhomMon()));
        monRepository.save(mon);
    }

    @Override
    @Transactional
    public void ngungBan(String maMon) {
        Mon mon = findById(maMon);
        mon.setTrangThai(Boolean.FALSE);
        monRepository.save(mon);
    }

    @Override
    @Transactional
    public void moBan(String maMon) {
        Mon mon = findById(maMon);
        mon.setTrangThai(Boolean.TRUE);
        monRepository.save(mon);
    }

    @Override
    @Transactional(readOnly = true)
    public String generateNextMaMon() {
        return CodeGeneratorUtil.generateNextCode(
                "M",
                monRepository.findAll().stream().map(Mon::getMaMon).collect(Collectors.toList()),
                2
        );
    }

    private NhomMon findNhomMon(String maNhomMon) {
        if (!StringUtils.hasText(maNhomMon)) {
            throw new IllegalArgumentException("Vui lòng chọn nhóm món.");
        }
        return nhomMonRepository.findById(maNhomMon.trim())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhóm món."));
    }

    private void validate(MonFormDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Dữ liệu món không hợp lệ.");
        }
        if (!StringUtils.hasText(dto.getTenMon())) {
            throw new IllegalArgumentException("Tên món không được để trống.");
        }
        if (dto.getDonGia() == null) {
            throw new IllegalArgumentException("Giá món không được để trống.");
        }
        if (dto.getDonGia().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá món không được âm.");
        }
        if (!StringUtils.hasText(dto.getMaNhomMon())) {
            throw new IllegalArgumentException("Món phải thuộc một nhóm món.");
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }

    private String resolveImageFileName(MultipartFile imageFile, String existingFileName, String codeBase) {
        if (imageFile == null || imageFile.isEmpty()) {
            return normalize(existingFileName);
        }

        String originalFileName = StringUtils.cleanPath(imageFile.getOriginalFilename() == null ? "" : imageFile.getOriginalFilename());
        String extension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFileName.substring(dotIndex);
        }

        String fileName = codeBase + "_" + UUID.randomUUID().toString().replace("-", "") + extension;
        Path uploadDir = Paths.get("uploads", "mon");

        try {
            Files.createDirectories(uploadDir);
            Files.copy(imageFile.getInputStream(), uploadDir.resolve(fileName), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ex) {
            throw new IllegalArgumentException("Không thể lưu hình ảnh: " + ex.getMessage());
        }
    }
}
