package com.hoangtuan.coffee_management.service.impl;

import com.hoangtuan.coffee_management.dto.ResetMatKhauDTO;
import com.hoangtuan.coffee_management.dto.TaiKhoanFormDTO;
import com.hoangtuan.coffee_management.dto.TaiKhoanListDTO;
import com.hoangtuan.coffee_management.entity.NhanVien;
import com.hoangtuan.coffee_management.entity.TaiKhoan;
import com.hoangtuan.coffee_management.entity.VaiTro;
import com.hoangtuan.coffee_management.repository.NhanVienRepository;
import com.hoangtuan.coffee_management.repository.TaiKhoanRepository;
import com.hoangtuan.coffee_management.service.TaiKhoanService;
import com.hoangtuan.coffee_management.service.VaiTroService;
import com.hoangtuan.coffee_management.util.CodeGeneratorUtil;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class TaiKhoanServiceImpl implements TaiKhoanService {

    private final TaiKhoanRepository taiKhoanRepository;
    private final NhanVienRepository nhanVienRepository;
    private final VaiTroService vaiTroService;
    private final PasswordEncoder passwordEncoder;

    public TaiKhoanServiceImpl(
            TaiKhoanRepository taiKhoanRepository,
            NhanVienRepository nhanVienRepository,
            VaiTroService vaiTroService,
            PasswordEncoder passwordEncoder
    ) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.vaiTroService = vaiTroService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaiKhoanListDTO> getDanhSach(String keyword, String trangThai, String vaiTro) {
        List<TaiKhoan> taiKhoans;
        String search = normalize(keyword);

        if (StringUtils.hasText(search)) {
            taiKhoans = taiKhoanRepository.findByMaTaiKhoanContainingIgnoreCaseOrTenDangNhapContainingIgnoreCaseOrNhanVien_HoTenContainingIgnoreCase(
                    search, search, search
            );
        } else {
            taiKhoans = taiKhoanRepository.findAll();
        }

        Boolean filterTrangThai = parseTrangThai(trangThai);
        String filterVaiTro = normalize(vaiTro);

        return taiKhoans.stream()
                .filter(taiKhoan -> filterTrangThai == null || Objects.equals(taiKhoan.getTrangThai(), filterTrangThai))
                .filter(taiKhoan -> !StringUtils.hasText(filterVaiTro) || "all".equalsIgnoreCase(filterVaiTro)
                        || (taiKhoan.getVaiTro() != null && filterVaiTro.equalsIgnoreCase(taiKhoan.getVaiTro().getTenVaiTro())))
                .sorted((left, right) -> safeString(left.getMaTaiKhoan()).compareToIgnoreCase(safeString(right.getMaTaiKhoan())))
                .map(this::toListDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NhanVien> getNhanVienChuaCoTaiKhoanOptions() {
        return nhanVienRepository.findAll().stream()
                .filter(nhanVien -> nhanVien.getTaiKhoan() == null)
                .sorted((left, right) -> safeString(left.getMaNhanVien()).compareToIgnoreCase(safeString(right.getMaNhanVien())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TaiKhoanFormDTO getFormThem() {
        String maTaiKhoan = CodeGeneratorUtil.generateNextCode(
                "TK",
                taiKhoanRepository.findAll().stream().map(TaiKhoan::getMaTaiKhoan).collect(Collectors.toList()),
                2
        );
        return new TaiKhoanFormDTO(maTaiKhoan, "", "", Boolean.TRUE, "ROLE_NHANVIEN", "", "");
    }

    @Override
    @Transactional(readOnly = true)
    public TaiKhoanFormDTO getFormSua(String maTaiKhoan) {
        TaiKhoan taiKhoan = findTaiKhoan(maTaiKhoan);
        NhanVien nhanVien = taiKhoan.getNhanVien();
        return new TaiKhoanFormDTO(
                taiKhoan.getMaTaiKhoan(),
                taiKhoan.getTenDangNhap(),
                "",
                taiKhoan.getTrangThai(),
                taiKhoan.getVaiTro() != null ? taiKhoan.getVaiTro().getTenVaiTro() : "",
                nhanVien != null ? nhanVien.getMaNhanVien() : "",
                nhanVien != null ? nhanVien.getHoTen() : ""
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ResetMatKhauDTO getFormResetMatKhau() {
        return new ResetMatKhauDTO("", "");
    }

    @Override
    @Transactional
    public void taoTaiKhoan(TaiKhoanFormDTO dto) {
        validateTaiKhoanForCreate(dto);
        String tenDangNhap = normalize(dto.getTenDangNhap());
        if (taiKhoanRepository.existsByTenDangNhap(tenDangNhap)) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại.");
        }

        NhanVien nhanVien = findNhanVienWithoutAccount(dto.getMaNhanVien());
        VaiTro vaiTro = resolveVaiTro(dto.getTenVaiTro());

        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setMaTaiKhoan(StringUtils.hasText(dto.getMaTaiKhoan())
                ? dto.getMaTaiKhoan().trim()
                : CodeGeneratorUtil.generateNextCode(
                "TK",
                taiKhoanRepository.findAll().stream().map(TaiKhoan::getMaTaiKhoan).collect(Collectors.toList()),
                2
        ));

        if (taiKhoanRepository.existsById(taiKhoan.getMaTaiKhoan())) {
            throw new IllegalArgumentException("Mã tài khoản đã tồn tại.");
        }

        taiKhoan.setTenDangNhap(tenDangNhap);
        taiKhoan.setMatKhau(passwordEncoder.encode(dto.getMatKhau()));
        taiKhoan.setTrangThai(dto.getTrangThai() == null || dto.getTrangThai());
        taiKhoan.setVaiTro(vaiTro);
        taiKhoan.setNhanVien(nhanVien);

        taiKhoanRepository.save(taiKhoan);
    }

    @Override
    @Transactional
    public void capNhatTaiKhoan(String maTaiKhoan, TaiKhoanFormDTO dto) {
        validateTaiKhoanForUpdate(dto);
        TaiKhoan taiKhoan = findTaiKhoan(maTaiKhoan);

        String tenDangNhapMoi = normalize(dto.getTenDangNhap());
        if (StringUtils.hasText(tenDangNhapMoi)
                && !tenDangNhapMoi.equalsIgnoreCase(taiKhoan.getTenDangNhap())
                && taiKhoanRepository.existsByTenDangNhapAndMaTaiKhoanNot(tenDangNhapMoi, taiKhoan.getMaTaiKhoan())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại.");
        }

        VaiTro vaiTroMoi = resolveVaiTro(dto.getTenVaiTro());
        boolean dangLaAdmin = taiKhoan.getVaiTro() != null && "ROLE_ADMIN".equalsIgnoreCase(taiKhoan.getVaiTro().getTenVaiTro());
        boolean troThanhKhongPhaiAdmin = vaiTroMoi != null && !"ROLE_ADMIN".equalsIgnoreCase(vaiTroMoi.getTenVaiTro());
        boolean biKhoa = dto.getTrangThai() != null && Boolean.FALSE.equals(dto.getTrangThai());

        if (dangLaAdmin && (troThanhKhongPhaiAdmin || biKhoa) && taiKhoanRepository.countByTrangThaiTrueAndVaiTro_TenVaiTro("ROLE_ADMIN") <= 1) {
            throw new IllegalArgumentException("Không thể vô hiệu hóa hoặc hạ quyền admin cuối cùng đang hoạt động.");
        }

        if (StringUtils.hasText(dto.getMaNhanVien()) && taiKhoan.getNhanVien() != null
                && !dto.getMaNhanVien().trim().equalsIgnoreCase(taiKhoan.getNhanVien().getMaNhanVien())) {
            throw new IllegalArgumentException("Không cho phép đổi nhân viên của tài khoản đã tồn tại.");
        }

        taiKhoan.setTenDangNhap(tenDangNhapMoi);
        taiKhoan.setTrangThai(dto.getTrangThai() == null || dto.getTrangThai());
        taiKhoan.setVaiTro(vaiTroMoi);
        taiKhoanRepository.save(taiKhoan);
    }

    @Override
    @Transactional
    public void khoaTaiKhoan(String maTaiKhoan, String currentUsername) {
        TaiKhoan taiKhoan = findTaiKhoan(maTaiKhoan);
        if (isAdmin(taiKhoan) && taiKhoanRepository.countByTrangThaiTrueAndVaiTro_TenVaiTro("ROLE_ADMIN") <= 1) {
            throw new IllegalArgumentException("Không thể khóa admin cuối cùng đang hoạt động.");
        }
        taiKhoan.setTrangThai(Boolean.FALSE);
        taiKhoanRepository.save(taiKhoan);
    }

    @Override
    @Transactional
    public void moKhoaTaiKhoan(String maTaiKhoan) {
        TaiKhoan taiKhoan = findTaiKhoan(maTaiKhoan);
        taiKhoan.setTrangThai(Boolean.TRUE);
        taiKhoanRepository.save(taiKhoan);
    }

    @Override
    @Transactional
    public void resetMatKhau(String maTaiKhoan, ResetMatKhauDTO dto) {
        validateResetMatKhau(dto);
        TaiKhoan taiKhoan = findTaiKhoan(maTaiKhoan);

        if (!Objects.equals(dto.getMatKhauMoi(), dto.getXacNhanMatKhauMoi())) {
            throw new IllegalArgumentException("Mật khẩu mới và xác nhận mật khẩu mới không khớp.");
        }

        taiKhoan.setMatKhau(passwordEncoder.encode(dto.getMatKhauMoi()));
        taiKhoanRepository.save(taiKhoan);
    }

    private TaiKhoanListDTO toListDTO(TaiKhoan taiKhoan) {
        NhanVien nhanVien = taiKhoan.getNhanVien();
        return new TaiKhoanListDTO(
                taiKhoan.getMaTaiKhoan(),
                taiKhoan.getTenDangNhap(),
                Boolean.TRUE.equals(taiKhoan.getTrangThai()) ? "Hoạt động" : "Bị khóa",
                taiKhoan.getVaiTro() != null ? taiKhoan.getVaiTro().getTenVaiTro() : "",
                nhanVien != null ? nhanVien.getMaNhanVien() : "",
                nhanVien != null ? nhanVien.getHoTen() : "",
                nhanVien != null ? nhanVien.getChucVu() : ""
        );
    }

    private NhanVien findNhanVienWithoutAccount(String maNhanVien) {
        if (!StringUtils.hasText(maNhanVien)) {
            throw new IllegalArgumentException("Vui lòng chọn nhân viên.");
        }
        NhanVien nhanVien = nhanVienRepository.findById(maNhanVien.trim())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên."));
        if (nhanVien.getTaiKhoan() != null) {
            throw new IllegalArgumentException("Nhân viên này đã có tài khoản.");
        }
        return nhanVien;
    }

    private VaiTro resolveVaiTro(String tenVaiTro) {
        if (!StringUtils.hasText(tenVaiTro)) {
            throw new IllegalArgumentException("Vui lòng chọn vai trò.");
        }
        return vaiTroService.getByTenVaiTro(tenVaiTro.trim());
    }

    private TaiKhoan findTaiKhoan(String maTaiKhoan) {
        return taiKhoanRepository.findById(maTaiKhoan)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản: " + maTaiKhoan));
    }

    private void validateTaiKhoanForCreate(TaiKhoanFormDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Dữ liệu tài khoản không hợp lệ.");
        }
        if (!StringUtils.hasText(dto.getTenDangNhap())) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống.");
        }
        if (!StringUtils.hasText(dto.getMatKhau())) {
            throw new IllegalArgumentException("Mật khẩu không được để trống.");
        }
        if (!StringUtils.hasText(dto.getTenVaiTro())) {
            throw new IllegalArgumentException("Vui lòng chọn vai trò.");
        }
    }

    private void validateTaiKhoanForUpdate(TaiKhoanFormDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Dữ liệu tài khoản không hợp lệ.");
        }
        if (!StringUtils.hasText(dto.getTenDangNhap())) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống.");
        }
        if (!StringUtils.hasText(dto.getTenVaiTro())) {
            throw new IllegalArgumentException("Vui lòng chọn vai trò.");
        }
    }

    private void validateResetMatKhau(ResetMatKhauDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Dữ liệu đổi mật khẩu không hợp lệ.");
        }
        if (!StringUtils.hasText(dto.getMatKhauMoi())) {
            throw new IllegalArgumentException("Mật khẩu mới không được để trống.");
        }
        if (!StringUtils.hasText(dto.getXacNhanMatKhauMoi())) {
            throw new IllegalArgumentException("Vui lòng xác nhận mật khẩu mới.");
        }
    }

    private boolean isAdmin(TaiKhoan taiKhoan) {
        return taiKhoan.getVaiTro() != null && "ROLE_ADMIN".equalsIgnoreCase(taiKhoan.getVaiTro().getTenVaiTro());
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private Boolean parseTrangThai(String trangThai) {
        if (!StringUtils.hasText(trangThai) || "all".equalsIgnoreCase(trangThai)) {
            return null;
        }
        return Boolean.valueOf(trangThai);
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }
}
