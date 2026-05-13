package com.hoangtuan.coffee_management.service.impl;

import com.hoangtuan.coffee_management.dto.NguyenLieuDetailDTO;
import com.hoangtuan.coffee_management.dto.NguyenLieuFormDTO;
import com.hoangtuan.coffee_management.dto.NguyenLieuListDTO;
import com.hoangtuan.coffee_management.dto.NguyenLieuSearchDTO;
import com.hoangtuan.coffee_management.entity.ChiTietPhieuNhap;
import com.hoangtuan.coffee_management.entity.ChiTietPhieuXuat;
import com.hoangtuan.coffee_management.entity.NguyenLieu;
import com.hoangtuan.coffee_management.repository.ChiTietPhieuNhapRepository;
import com.hoangtuan.coffee_management.repository.ChiTietPhieuXuatRepository;
import com.hoangtuan.coffee_management.repository.NguyenLieuRepository;
import com.hoangtuan.coffee_management.service.NguyenLieuService;
import com.hoangtuan.coffee_management.util.CodeGeneratorUtil;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class NguyenLieuServiceImpl implements NguyenLieuService {

    private final NguyenLieuRepository nguyenLieuRepository;
    private final ChiTietPhieuNhapRepository chiTietPhieuNhapRepository;
    private final ChiTietPhieuXuatRepository chiTietPhieuXuatRepository;

    public NguyenLieuServiceImpl(
            NguyenLieuRepository nguyenLieuRepository,
            ChiTietPhieuNhapRepository chiTietPhieuNhapRepository,
            ChiTietPhieuXuatRepository chiTietPhieuXuatRepository
    ) {
        this.nguyenLieuRepository = nguyenLieuRepository;
        this.chiTietPhieuNhapRepository = chiTietPhieuNhapRepository;
        this.chiTietPhieuXuatRepository = chiTietPhieuXuatRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NguyenLieu> findAll() {
        return nguyenLieuRepository.findAll().stream()
                .sorted(Comparator.comparing(n -> safeString(n.getMaNguyenLieu()), String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NguyenLieu> search(String keyword) {
        String search = normalize(keyword);
        if (!StringUtils.hasText(search)) {
            return findAll();
        }
        return nguyenLieuRepository.findByMaNguyenLieuContainingIgnoreCaseOrTenNguyenLieuContainingIgnoreCase(search, search).stream()
                .sorted(Comparator.comparing(n -> safeString(n.getMaNguyenLieu()), String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NguyenLieu> findCanhBaoSapHet() {
        return nguyenLieuRepository.findNguyenLieuSapHet().stream()
                .sorted(Comparator.comparing(n -> safeString(n.getMaNguyenLieu()), String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NguyenLieuListDTO> getDanhSach(NguyenLieuSearchDTO searchDTO) {
        String keyword = searchDTO != null ? searchDTO.getKeyword() : null;
        Boolean sapHetOnly = searchDTO != null ? searchDTO.getSapHet() : null;

        return search(keyword).stream()
                .filter(nguyenLieu -> sapHetOnly == null
                        || (sapHetOnly && kiemTraSapHet(nguyenLieu))
                        || (!sapHetOnly && !kiemTraSapHet(nguyenLieu)))
                .map(this::toListDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public NguyenLieu findById(String maNguyenLieu) {
        if (!StringUtils.hasText(maNguyenLieu)) {
            throw new IllegalArgumentException("Vui long chon nguyen lieu.");
        }
        return nguyenLieuRepository.findById(maNguyenLieu.trim())
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nguyen lieu."));
    }

    @Override
    @Transactional(readOnly = true)
    public NguyenLieuFormDTO getFormThem() {
        return new NguyenLieuFormDTO(generateNextMaNguyenLieu(), "", "", 0, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public NguyenLieuFormDTO getFormSua(String maNguyenLieu) {
        NguyenLieu nguyenLieu = findById(maNguyenLieu);
        return new NguyenLieuFormDTO(
                nguyenLieu.getMaNguyenLieu(),
                nguyenLieu.getTenNguyenLieu(),
                nguyenLieu.getDonViTinh(),
                nguyenLieu.getSoLuongTon(),
                nguyenLieu.getMucTonToiThieu()
        );
    }

    @Override
    @Transactional
    public void save(NguyenLieuFormDTO dto) {
        validate(dto);
        String maNguyenLieu = StringUtils.hasText(dto.getMaNguyenLieu())
                ? dto.getMaNguyenLieu().trim()
                : generateNextMaNguyenLieu();
        if (nguyenLieuRepository.existsById(maNguyenLieu)) {
            throw new IllegalArgumentException("Ma nguyen lieu da ton tai.");
        }
        NguyenLieu nguyenLieu = new NguyenLieu();
        nguyenLieu.setMaNguyenLieu(maNguyenLieu);
        applyForm(nguyenLieu, dto, true);
        nguyenLieuRepository.save(nguyenLieu);
    }

    @Override
    @Transactional
    public void update(String maNguyenLieu, NguyenLieuFormDTO dto) {
        validate(dto);
        NguyenLieu nguyenLieu = findById(maNguyenLieu);
        applyForm(nguyenLieu, dto, false);
        nguyenLieuRepository.save(nguyenLieu);
    }

    @Override
    @Transactional
    public void tangTonKho(String maNguyenLieu, Integer soLuongNhap) {
        if (soLuongNhap == null || soLuongNhap <= 0) {
            throw new IllegalArgumentException("So luong nhap phai lon hon 0.");
        }
        NguyenLieu nguyenLieu = findById(maNguyenLieu);
        nguyenLieu.setSoLuongTon((nguyenLieu.getSoLuongTon() == null ? 0 : nguyenLieu.getSoLuongTon()) + soLuongNhap);
        nguyenLieuRepository.save(nguyenLieu);
    }

    @Override
    @Transactional(readOnly = true)
    public NguyenLieuDetailDTO getChiTiet(String maNguyenLieu) {
        NguyenLieu nguyenLieu = findById(maNguyenLieu);
        boolean sapHet = kiemTraSapHet(nguyenLieu);

        List<ChiTietPhieuNhap> dsNhap = chiTietPhieuNhapRepository.findByNguyenLieu_MaNguyenLieu(maNguyenLieu).stream()
                .sorted(Comparator.comparing((ChiTietPhieuNhap item) -> item.getPhieuNhapKho().getNgayNhap()).reversed())
                .collect(Collectors.toList());
        List<ChiTietPhieuXuat> dsXuat = chiTietPhieuXuatRepository.findByNguyenLieu_MaNguyenLieu(maNguyenLieu).stream()
                .sorted(Comparator.comparing((ChiTietPhieuXuat item) -> item.getPhieuXuatKho().getNgayXuat()).reversed())
                .collect(Collectors.toList());

        long tongSoLuongNhap = dsNhap.stream().mapToLong(item -> item.getSoLuongNhap() == null ? 0 : item.getSoLuongNhap()).sum();
        long tongSoLuongXuat = dsXuat.stream().mapToLong(item -> item.getSoLuongXuat() == null ? 0 : item.getSoLuongXuat()).sum();

        List<String> nhapGanNhat = dsNhap.stream()
                .limit(5)
                .map(item -> String.format("%s | %s | +%d %s",
                        safeString(item.getMaCTPN()),
                        item.getPhieuNhapKho() != null && item.getPhieuNhapKho().getNgayNhap() != null
                                ? item.getPhieuNhapKho().getNgayNhap().toString()
                                : "Khong ro ngay",
                        item.getSoLuongNhap() == null ? 0 : item.getSoLuongNhap(),
                        safeString(item.getNguyenLieu() != null ? item.getNguyenLieu().getDonViTinh() : nguyenLieu.getDonViTinh())))
                .collect(Collectors.toList());

        List<String> xuatGanNhat = dsXuat.stream()
                .limit(5)
                .map(item -> String.format("%s | %s | -%d %s",
                        safeString(item.getMaCTPX()),
                        item.getPhieuXuatKho() != null && item.getPhieuXuatKho().getNgayXuat() != null
                                ? item.getPhieuXuatKho().getNgayXuat().toString()
                                : "Khong ro ngay",
                        item.getSoLuongXuat() == null ? 0 : item.getSoLuongXuat(),
                        safeString(item.getNguyenLieu() != null ? item.getNguyenLieu().getDonViTinh() : nguyenLieu.getDonViTinh())))
                .collect(Collectors.toList());

        return new NguyenLieuDetailDTO(
                nguyenLieu,
                sapHet,
                dsNhap.size(),
                tongSoLuongNhap,
                dsXuat.size(),
                tongSoLuongXuat,
                nhapGanNhat,
                xuatGanNhat
        );
    }

    @Override
    public boolean kiemTraSapHet(NguyenLieu nguyenLieu) {
        if (nguyenLieu == null) {
            return false;
        }
        Integer soLuongTon = nguyenLieu.getSoLuongTon();
        Integer mucTonToiThieu = nguyenLieu.getMucTonToiThieu();
        return soLuongTon != null && mucTonToiThieu != null && soLuongTon <= mucTonToiThieu;
    }

    @Override
    @Transactional(readOnly = true)
    public String generateNextMaNguyenLieu() {
        return CodeGeneratorUtil.generateNextCode(
                "NL",
                nguyenLieuRepository.findAll().stream().map(NguyenLieu::getMaNguyenLieu).collect(Collectors.toList()),
                3
        );
    }

    private void validate(NguyenLieuFormDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Du lieu nguyen lieu khong hop le.");
        }
        if (!StringUtils.hasText(dto.getTenNguyenLieu())) {
            throw new IllegalArgumentException("Ten nguyen lieu khong duoc de trong.");
        }
        if (!StringUtils.hasText(dto.getDonViTinh())) {
            throw new IllegalArgumentException("Don vi tinh khong duoc de trong.");
        }
        if (dto.getSoLuongTon() == null) {
            throw new IllegalArgumentException("So luong ton khong duoc de trong.");
        }
        if (dto.getSoLuongTon() < 0) {
            throw new IllegalArgumentException("So luong ton khong duoc am.");
        }
        if (dto.getMucTonToiThieu() == null) {
            throw new IllegalArgumentException("Muc ton toi thieu khong duoc de trong.");
        }
        if (dto.getMucTonToiThieu() < 0) {
            throw new IllegalArgumentException("Muc ton toi thieu khong duoc am.");
        }
    }

    private void applyForm(NguyenLieu nguyenLieu, NguyenLieuFormDTO dto, boolean create) {
        nguyenLieu.setTenNguyenLieu(normalize(dto.getTenNguyenLieu()));
        nguyenLieu.setDonViTinh(normalize(dto.getDonViTinh()));
        nguyenLieu.setMucTonToiThieu(dto.getMucTonToiThieu());
        if (create) {
            nguyenLieu.setSoLuongTon(dto.getSoLuongTon());
        }
    }

    private NguyenLieuListDTO toListDTO(NguyenLieu nguyenLieu) {
        boolean sapHet = kiemTraSapHet(nguyenLieu);
        return new NguyenLieuListDTO(
                nguyenLieu.getMaNguyenLieu(),
                nguyenLieu.getTenNguyenLieu(),
                nguyenLieu.getDonViTinh(),
                nguyenLieu.getSoLuongTon(),
                nguyenLieu.getMucTonToiThieu(),
                sapHet,
                sapHet ? "Sắp hết" : "Đủ hàng",
                sapHet ? "badge-status--warning" : "badge-status--success"
        );
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }
}
