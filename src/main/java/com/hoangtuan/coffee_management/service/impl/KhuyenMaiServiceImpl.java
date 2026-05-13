package com.hoangtuan.coffee_management.service.impl;

import com.hoangtuan.coffee_management.dto.KhuyenMaiDetailDTO;
import com.hoangtuan.coffee_management.dto.KhuyenMaiFormDTO;
import com.hoangtuan.coffee_management.dto.KhuyenMaiListDTO;
import com.hoangtuan.coffee_management.entity.HoaDon;
import com.hoangtuan.coffee_management.entity.KhuyenMai;
import com.hoangtuan.coffee_management.repository.HoaDonRepository;
import com.hoangtuan.coffee_management.repository.KhuyenMaiRepository;
import com.hoangtuan.coffee_management.service.KhuyenMaiService;
import com.hoangtuan.coffee_management.util.CodeGeneratorUtil;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class KhuyenMaiServiceImpl implements KhuyenMaiService {

    private final KhuyenMaiRepository khuyenMaiRepository;
    private final HoaDonRepository hoaDonRepository;

    public KhuyenMaiServiceImpl(KhuyenMaiRepository khuyenMaiRepository, HoaDonRepository hoaDonRepository) {
        this.khuyenMaiRepository = khuyenMaiRepository;
        this.hoaDonRepository = hoaDonRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<KhuyenMai> findAll() {
        return khuyenMaiRepository.findAll().stream()
                .sorted((left, right) -> safeString(left.getMaKhuyenMai()).compareToIgnoreCase(safeString(right.getMaKhuyenMai())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<KhuyenMaiListDTO> getDanhSach(String keyword, String loaiKhuyenMai, Boolean trangThai) {
        return searchAndFilter(keyword, loaiKhuyenMai, trangThai).stream()
                .map(this::toListDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<KhuyenMai> searchAndFilter(String keyword, String loaiKhuyenMai, Boolean trangThai) {
        String search = normalize(keyword);
        String loai = normalize(loaiKhuyenMai);
        Boolean status = trangThai;

        return khuyenMaiRepository.findAll().stream()
                .filter(khuyenMai -> !StringUtils.hasText(search)
                        || safeString(khuyenMai.getMaKhuyenMai()).toLowerCase().contains(search.toLowerCase())
                        || safeString(khuyenMai.getTenKhuyenMai()).toLowerCase().contains(search.toLowerCase()))
                .filter(khuyenMai -> !StringUtils.hasText(loai)
                        || "all".equalsIgnoreCase(loai)
                        || safeString(khuyenMai.getLoaiKhuyenMai()).equalsIgnoreCase(loai))
                .filter(khuyenMai -> status == null || Objects.equals(khuyenMai.getTrangThai(), status))
                .sorted((left, right) -> safeString(left.getMaKhuyenMai()).compareToIgnoreCase(safeString(right.getMaKhuyenMai())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<KhuyenMai> findKhuyenMaiConHieuLuc() {
        LocalDateTime now = LocalDateTime.now();
        return khuyenMaiRepository.findByTrangThai(Boolean.TRUE).stream()
                .filter(khuyenMai -> khuyenMai.getNgayBatDau() == null || !khuyenMai.getNgayBatDau().isAfter(now))
                .filter(khuyenMai -> khuyenMai.getNgayKetThuc() == null || !khuyenMai.getNgayKetThuc().isBefore(now))
                .sorted((left, right) -> safeString(left.getMaKhuyenMai()).compareToIgnoreCase(safeString(right.getMaKhuyenMai())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public KhuyenMai findById(String maKhuyenMai) {
        if (!StringUtils.hasText(maKhuyenMai)) {
            throw new IllegalArgumentException("Vui long chon khuyen mai.");
        }
        return khuyenMaiRepository.findById(maKhuyenMai.trim())
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay khuyen mai."));
    }

    @Override
    @Transactional(readOnly = true)
    public KhuyenMaiFormDTO getFormThem() {
        LocalDateTime now = LocalDateTime.now();
        return new KhuyenMaiFormDTO(generateNextMaKhuyenMai(), "", "GIAM_TIEN", BigDecimal.ZERO, now, now.plusDays(7), Boolean.TRUE);
    }

    @Override
    @Transactional(readOnly = true)
    public KhuyenMaiFormDTO getFormSua(String maKhuyenMai) {
        KhuyenMai khuyenMai = findById(maKhuyenMai);
        return new KhuyenMaiFormDTO(
                khuyenMai.getMaKhuyenMai(),
                khuyenMai.getTenKhuyenMai(),
                khuyenMai.getLoaiKhuyenMai(),
                khuyenMai.getGiaTriGiam(),
                khuyenMai.getNgayBatDau(),
                khuyenMai.getNgayKetThuc(),
                khuyenMai.getTrangThai()
        );
    }

    @Override
    @Transactional
    public void save(KhuyenMaiFormDTO dto) {
        validate(dto);
        String maKhuyenMai = StringUtils.hasText(dto.getMaKhuyenMai()) ? dto.getMaKhuyenMai().trim() : generateNextMaKhuyenMai();
        if (khuyenMaiRepository.existsById(maKhuyenMai)) {
            throw new IllegalArgumentException("Ma khuyen mai da ton tai.");
        }
        KhuyenMai khuyenMai = buildKhuyenMai(new KhuyenMai(), maKhuyenMai, dto);
        khuyenMaiRepository.save(khuyenMai);
    }

    @Override
    @Transactional
    public void update(String maKhuyenMai, KhuyenMaiFormDTO dto) {
        validate(dto);
        KhuyenMai khuyenMai = findById(maKhuyenMai);
        buildKhuyenMai(khuyenMai, maKhuyenMai, dto);
        khuyenMaiRepository.save(khuyenMai);
    }

    @Override
    @Transactional
    public void ngungApDung(String maKhuyenMai) {
        KhuyenMai khuyenMai = findById(maKhuyenMai);
        khuyenMai.setTrangThai(Boolean.FALSE);
        khuyenMaiRepository.save(khuyenMai);
    }

    @Override
    @Transactional
    public void kichHoat(String maKhuyenMai) {
        KhuyenMai khuyenMai = findById(maKhuyenMai);
        khuyenMai.setTrangThai(Boolean.TRUE);
        khuyenMaiRepository.save(khuyenMai);
    }

    @Override
    @Transactional(readOnly = true)
    public KhuyenMaiDetailDTO getChiTiet(String maKhuyenMai) {
        KhuyenMai khuyenMai = findById(maKhuyenMai);
        List<HoaDon> hoaDons = hoaDonRepository.findByKhuyenMai_MaKhuyenMai(maKhuyenMai);
        BigDecimal tongTienDaGiam = hoaDons.stream()
                .map(hoaDon -> hoaDon.getSoTienGiam() == null ? BigDecimal.ZERO : hoaDon.getSoTienGiam())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new KhuyenMaiDetailDTO(khuyenMai, hoaDons.size(), tongTienDaGiam, kiemTraConHieuLuc(khuyenMai));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean kiemTraConHieuLuc(KhuyenMai khuyenMai) {
        if (khuyenMai == null || !Boolean.TRUE.equals(khuyenMai.getTrangThai())) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return (khuyenMai.getNgayBatDau() == null || !khuyenMai.getNgayBatDau().isAfter(now))
                && (khuyenMai.getNgayKetThuc() == null || !khuyenMai.getNgayKetThuc().isBefore(now));
    }

    @Override
    @Transactional(readOnly = true)
    public String generateNextMaKhuyenMai() {
        return CodeGeneratorUtil.generateNextCode(
                "KM",
                khuyenMaiRepository.findAll().stream().map(KhuyenMai::getMaKhuyenMai).collect(Collectors.toList()),
                3
        );
    }

    private KhuyenMai buildKhuyenMai(KhuyenMai khuyenMai, String maKhuyenMai, KhuyenMaiFormDTO dto) {
        khuyenMai.setMaKhuyenMai(maKhuyenMai);
        khuyenMai.setTenKhuyenMai(normalize(dto.getTenKhuyenMai()));
        khuyenMai.setLoaiKhuyenMai(validateLoai(dto.getLoaiKhuyenMai()));
        khuyenMai.setGiaTriGiam(validateGiaTri(dto.getGiaTriGiam(), khuyenMai.getLoaiKhuyenMai()));
        khuyenMai.setNgayBatDau(dto.getNgayBatDau());
        khuyenMai.setNgayKetThuc(dto.getNgayKetThuc());
        khuyenMai.setTrangThai(dto.getTrangThai() == null || dto.getTrangThai());
        return khuyenMai;
    }

    private void validate(KhuyenMaiFormDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Du lieu khuyen mai khong hop le.");
        }
        if (!StringUtils.hasText(dto.getTenKhuyenMai())) {
            throw new IllegalArgumentException("Ten khuyen mai khong duoc de trong.");
        }
        if (dto.getGiaTriGiam() == null) {
            throw new IllegalArgumentException("Gia tri giam khong duoc de trong.");
        }
        if (dto.getGiaTriGiam().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Gia tri giam phai lon hon 0.");
        }
        if (dto.getNgayBatDau() == null || dto.getNgayKetThuc() == null) {
            throw new IllegalArgumentException("Vui long chon ngay bat dau va ngay ket thuc.");
        }
        if (dto.getNgayBatDau().isAfter(dto.getNgayKetThuc())) {
            throw new IllegalArgumentException("Ngay bat dau khong duoc sau ngay ket thuc.");
        }
        validateLoai(dto.getLoaiKhuyenMai());
        if ("GIAM_PHAN_TRAM".equalsIgnoreCase(normalize(dto.getLoaiKhuyenMai()))
                && dto.getGiaTriGiam().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Khuyen mai phan tram khong duoc lon hon 100.");
        }
    }

    private String validateLoai(String loaiKhuyenMai) {
        String normalized = normalize(loaiKhuyenMai);
        if (!Arrays.asList("GIAM_TIEN", "GIAM_PHAN_TRAM").contains(normalized)) {
            throw new IllegalArgumentException("Loai khuyen mai chi duoc la GIAM_TIEN hoac GIAM_PHAN_TRAM.");
        }
        return normalized;
    }

    private BigDecimal validateGiaTri(BigDecimal giaTriGiam, String loaiKhuyenMai) {
        if (giaTriGiam == null || giaTriGiam.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Gia tri giam phai lon hon 0.");
        }
        if ("GIAM_PHAN_TRAM".equalsIgnoreCase(loaiKhuyenMai) && giaTriGiam.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Khuyen mai phan tram khong duoc lon hon 100.");
        }
        return giaTriGiam;
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private KhuyenMaiListDTO toListDTO(KhuyenMai khuyenMai) {
        boolean conHieuLuc = kiemTraConHieuLuc(khuyenMai);
        String trangThaiHienThi;
        String badgeClass;
        if (!Boolean.TRUE.equals(khuyenMai.getTrangThai())) {
            trangThaiHienThi = "Ngừng áp dụng";
            badgeClass = "badge-status--neutral";
        } else if (!conHieuLuc) {
            trangThaiHienThi = "Hết hạn";
            badgeClass = "badge-status--danger";
        } else {
            trangThaiHienThi = "Đang áp dụng";
            badgeClass = "badge-status--success";
        }
        return new KhuyenMaiListDTO(
                khuyenMai.getMaKhuyenMai(),
                khuyenMai.getTenKhuyenMai(),
                khuyenMai.getLoaiKhuyenMai(),
                khuyenMai.getGiaTriGiam(),
                khuyenMai.getNgayBatDau(),
                khuyenMai.getNgayKetThuc(),
                khuyenMai.getTrangThai(),
                conHieuLuc,
                trangThaiHienThi,
                badgeClass
        );
    }
}
