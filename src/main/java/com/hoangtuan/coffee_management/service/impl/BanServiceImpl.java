package com.hoangtuan.coffee_management.service.impl;

import com.hoangtuan.coffee_management.dto.BanFormDTO;
import com.hoangtuan.coffee_management.dto.BanSearchDTO;
import com.hoangtuan.coffee_management.entity.Ban;
import com.hoangtuan.coffee_management.repository.BanRepository;
import com.hoangtuan.coffee_management.service.BanService;
import com.hoangtuan.coffee_management.util.CodeGeneratorUtil;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class BanServiceImpl implements BanService {

    public static final String TRONG = "Trống";
    public static final String DANG_PHUC_VU = "Đang phục vụ";
    public static final String CHO_THANH_TOAN = "Chờ thanh toán";

    private final BanRepository banRepository;

    public BanServiceImpl(BanRepository banRepository) {
        this.banRepository = banRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ban> findAll() {
        return banRepository.findAll().stream()
                .sorted((left, right) -> safeString(left.getMaBan()).compareToIgnoreCase(safeString(right.getMaBan())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Ban findById(String maBan) {
        return banRepository.findById(maBan)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bàn: " + maBan));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ban> searchAndFilter(BanSearchDTO searchDTO) {
        final String keyword = searchDTO != null ? normalize(searchDTO.getKeyword()) : null;
        final String trangThai = searchDTO != null ? normalize(searchDTO.getTrangThai()) : null;

        return banRepository.findAll().stream()
                .filter(ban -> !StringUtils.hasText(keyword)
                        || safeString(ban.getMaBan()).toLowerCase().contains(keyword.toLowerCase()
                        ) || safeString(ban.getTenBan()).toLowerCase().contains(keyword.toLowerCase())
                        || safeString(ban.getKhuVuc()).toLowerCase().contains(keyword.toLowerCase()))
                .filter(ban -> !StringUtils.hasText(trangThai)
                        || "all".equalsIgnoreCase(trangThai)
                        || Objects.equals(normalizeTrangThai(ban.getTrangThai()), normalizeTrangThai(trangThai)))
                .sorted((left, right) -> safeString(left.getMaBan()).compareToIgnoreCase(safeString(right.getMaBan())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ban> findByTrangThai(String trangThai) {
        return banRepository.findByTrangThai(normalizeTrangThai(trangThai));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ban> findBanTrong() {
        return banRepository.findByTrangThai(TRONG);
    }

    @Override
    @Transactional(readOnly = true)
    public BanFormDTO getFormThem() {
        return new BanFormDTO(generateNextMaBan(), "", "", TRONG, "");
    }

    @Override
    @Transactional(readOnly = true)
    public BanFormDTO getFormSua(String maBan) {
        Ban ban = findById(maBan);
        return new BanFormDTO(ban.getMaBan(), ban.getTenBan(), ban.getKhuVuc(), ban.getTrangThai(), ban.getGhiChu());
    }

    @Override
    @Transactional
    public void save(BanFormDTO dto) {
        validate(dto);
        String maBan = StringUtils.hasText(dto.getMaBan())
                ? dto.getMaBan().trim()
                : generateNextMaBan();
        if (banRepository.existsById(maBan)) {
            throw new IllegalArgumentException("Mã bàn đã tồn tại.");
        }
        Ban ban = new Ban(maBan, normalize(dto.getTenBan()), normalize(dto.getKhuVuc()), normalizeTrangThai(dto.getTrangThai()), normalize(dto.getGhiChu()));
        banRepository.save(ban);
    }

    @Override
    @Transactional
    public void update(String maBan, BanFormDTO dto) {
        validate(dto);
        Ban ban = findById(maBan);
        ban.setTenBan(normalize(dto.getTenBan()));
        ban.setKhuVuc(normalize(dto.getKhuVuc()));
        ban.setTrangThai(normalizeTrangThai(dto.getTrangThai()));
        ban.setGhiChu(normalize(dto.getGhiChu()));
        banRepository.save(ban);
    }

    @Override
    @Transactional
    public void updateTrangThai(String maBan, String trangThai) {
        Ban ban = findById(maBan);
        ban.setTrangThai(validateTrangThai(trangThai));
        banRepository.save(ban);
    }

    @Override
    @Transactional(readOnly = true)
    public String generateNextMaBan() {
        return CodeGeneratorUtil.generateNextCode(
                "B",
                banRepository.findAll().stream().map(Ban::getMaBan).collect(Collectors.toList()),
                2
        );
    }

    private void validate(BanFormDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Dữ liệu bàn không hợp lệ.");
        }
        if (!StringUtils.hasText(dto.getTenBan())) {
            throw new IllegalArgumentException("Tên bàn không được để trống.");
        }
        if (!StringUtils.hasText(dto.getTrangThai())) {
            throw new IllegalArgumentException("Trạng thái bàn không được để trống.");
        }
        validateTrangThai(dto.getTrangThai());
    }

    private String validateTrangThai(String trangThai) {
        String normalized = normalize(trangThai);
        if (!StringUtils.hasText(normalized)) {
            throw new IllegalArgumentException("Trạng thái bàn không được để trống.");
        }
        List<String> allowed = Arrays.asList(TRONG, DANG_PHUC_VU, CHO_THANH_TOAN);
        if (!allowed.contains(normalized)) {
            throw new IllegalArgumentException("Trạng thái bàn chỉ được là: Trống, Đang phục vụ, Chờ thanh toán.");
        }
        return normalized;
    }

    private String normalizeTrangThai(String trangThai) {
        return validateTrangThai(trangThai);
    }

    private String normalizeTrangThai(String trangThai, boolean allowNull) {
        if (!StringUtils.hasText(trangThai) && allowNull) {
            return null;
        }
        return validateTrangThai(trangThai);
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }
}
