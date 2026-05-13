package com.hoangtuan.coffee_management.service.impl;

import com.hoangtuan.coffee_management.dto.NhomMonFormDTO;
import com.hoangtuan.coffee_management.entity.NhomMon;
import com.hoangtuan.coffee_management.repository.NhomMonRepository;
import com.hoangtuan.coffee_management.service.NhomMonService;
import com.hoangtuan.coffee_management.util.CodeGeneratorUtil;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class NhomMonServiceImpl implements NhomMonService {

    private final NhomMonRepository nhomMonRepository;

    public NhomMonServiceImpl(NhomMonRepository nhomMonRepository) {
        this.nhomMonRepository = nhomMonRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NhomMon> findAll() {
        return nhomMonRepository.findAll().stream()
                .sorted((left, right) -> safeString(left.getMaNhomMon()).compareToIgnoreCase(safeString(right.getMaNhomMon())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public NhomMon findById(String maNhomMon) {
        return nhomMonRepository.findById(maNhomMon)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhóm món: " + maNhomMon));
    }

    @Override
    @Transactional(readOnly = true)
    public NhomMonFormDTO getFormThem() {
        return new NhomMonFormDTO(generateNextMaNhomMon(), "", "");
    }

    @Override
    @Transactional(readOnly = true)
    public NhomMonFormDTO getFormSua(String maNhomMon) {
        NhomMon nhomMon = findById(maNhomMon);
        return new NhomMonFormDTO(nhomMon.getMaNhomMon(), nhomMon.getTenNhomMon(), nhomMon.getMoTa());
    }

    @Override
    @Transactional
    public void save(NhomMonFormDTO dto) {
        validate(dto);
        String maNhomMon = StringUtils.hasText(dto.getMaNhomMon())
                ? dto.getMaNhomMon().trim()
                : generateNextMaNhomMon();

        if (nhomMonRepository.existsById(maNhomMon)) {
            throw new IllegalArgumentException("Mã nhóm món đã tồn tại.");
        }
        if (nhomMonRepository.existsByTenNhomMonIgnoreCase(normalize(dto.getTenNhomMon()))) {
            throw new IllegalArgumentException("Tên nhóm món đã tồn tại.");
        }

        NhomMon nhomMon = new NhomMon(maNhomMon, normalize(dto.getTenNhomMon()), normalize(dto.getMoTa()));
        nhomMonRepository.save(nhomMon);
    }

    @Override
    @Transactional
    public void update(String maNhomMon, NhomMonFormDTO dto) {
        validate(dto);
        NhomMon nhomMon = findById(maNhomMon);
        String tenMoi = normalize(dto.getTenNhomMon());
        if (nhomMonRepository.existsByTenNhomMonIgnoreCaseAndMaNhomMonNot(tenMoi, nhomMon.getMaNhomMon())) {
            throw new IllegalArgumentException("Tên nhóm món đã tồn tại.");
        }
        nhomMon.setTenNhomMon(tenMoi);
        nhomMon.setMoTa(normalize(dto.getMoTa()));
        nhomMonRepository.save(nhomMon);
    }

    @Override
    @Transactional(readOnly = true)
    public String generateNextMaNhomMon() {
        return CodeGeneratorUtil.generateNextCode(
                "NM",
                nhomMonRepository.findAll().stream().map(NhomMon::getMaNhomMon).collect(Collectors.toList()),
                2
        );
    }

    private void validate(NhomMonFormDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Dữ liệu nhóm món không hợp lệ.");
        }
        if (!StringUtils.hasText(dto.getTenNhomMon())) {
            throw new IllegalArgumentException("Tên nhóm món không được để trống.");
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }
}
