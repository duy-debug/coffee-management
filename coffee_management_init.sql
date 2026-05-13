CREATE DATABASE IF NOT EXISTS coffee_hoang_tuan
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE coffee_hoang_tuan;

- - =========================
-- 1. VAI TRÒ CỐ ĐỊNH
-- =========================
CREATE TABLE VaiTro (
maVaiTro VARCHAR(10) PRIMARY KEY,
tenVaiTro VARCHAR(50) NOT NULL,
moTa VARCHAR(255)
);
- - =========================
-- 2. NHÂN VIÊN
-- =========================
CREATE TABLE NhanVien (
maNhanVien VARCHAR(10) PRIMARY KEY,
hoTen VARCHAR(100) NOT NULL,
soDienThoai VARCHAR(15) NOT NULL,
diaChi VARCHAR(255),
chucVu VARCHAR(50) NOT NULL,
trangThai BOOLEAN NOT NULL DEFAULT TRUE
);
- - =========================
-- 3. TÀI KHOẢN
-- Mỗi tài khoản thuộc một vai trò cố định
-- =========================
CREATE TABLE TaiKhoan (
maTaiKhoan VARCHAR(10) PRIMARY KEY,
tenDangNhap VARCHAR(50) NOT NULL UNIQUE,
matKhau VARCHAR(255) NOT NULL,
trangThai BOOLEAN NOT NULL DEFAULT TRUE,
maVaiTro VARCHAR(10) NOT NULL,
maNhanVien VARCHAR(10) UNIQUE,
FOREIGN KEY (maVaiTro) REFERENCES VaiTro(maVaiTro)
ON DELETE RESTRICT ON UPDATE CASCADE,
FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien)
ON DELETE SET NULL ON UPDATE CASCADE
);
- - =========================
-- 4. KHÁCH HÀNG
-- =========================
CREATE TABLE KhachHang (
maKhachHang VARCHAR(10) PRIMARY KEY,
hoTen VARCHAR(100) NOT NULL,
soDienThoai VARCHAR(15),
ghiChu VARCHAR(255)
);
- - =========================
-- 5. BÀN
-- =========================
CREATE TABLE Ban (
maBan VARCHAR(10) PRIMARY KEY,
tenBan VARCHAR(50) NOT NULL,
khuVuc VARCHAR(50),
trangThai VARCHAR(50) NOT NULL DEFAULT 'Trống',
ghiChu VARCHAR(255),
CONSTRAINT chk_trang_thai_ban
CHECK (trangThai IN ('Trống', 'Đang phục vụ', 'Chờ thanh toán'))
);
- - =========================
-- 6. KHUYẾN MÃI
-- =========================
CREATE TABLE KhuyenMai (
maKhuyenMai VARCHAR(10) PRIMARY KEY,
tenKhuyenMai VARCHAR(100) NOT NULL,
loaiKhuyenMai VARCHAR(50) NOT NULL,
giaTriGiam DECIMAL(18,2) NOT NULL DEFAULT 0,
ngayBatDau DATETIME NOT NULL,
ngayKetThuc DATETIME NOT NULL,
trangThai BOOLEAN NOT NULL DEFAULT TRUE
);
- - =========================
-- 7. NHÓM MÓN
-- =========================
CREATE TABLE NhomMon (
maNhomMon VARCHAR(10) PRIMARY KEY,
tenNhomMon VARCHAR(50) NOT NULL,
moTa VARCHAR(255)
);
- - =========================
-- 8. MÓN
-- =========================
CREATE TABLE Mon (
maMon VARCHAR(10) PRIMARY KEY,
tenMon VARCHAR(100) NOT NULL,
donGia DECIMAL(18,2) NOT NULL,
moTa VARCHAR(255),
hinhAnh VARCHAR(255),
trangThai BOOLEAN NOT NULL DEFAULT TRUE,
maNhomMon VARCHAR(10) NOT NULL,
FOREIGN KEY (maNhomMon) REFERENCES NhomMon(maNhomMon)
ON DELETE RESTRICT ON UPDATE CASCADE
);
- - =========================
-- 9. ĐƠN HÀNG
-- Đơn dùng tại quán có maBan
-- Đơn mang đi có thể không có maBan
-- =========================
CREATE TABLE DonHang (
maDonHang VARCHAR(10) PRIMARY KEY,
ngayDat DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
loaiDonHang VARCHAR(50) NOT NULL,
trangThai VARCHAR(50) NOT NULL DEFAULT 'Đang xử lý',
tongTien DECIMAL(18,2) NOT NULL DEFAULT 0,
maNhanVien VARCHAR(10) NOT NULL,
maKhachHang VARCHAR(10),
maBan VARCHAR(10),
FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien)
ON DELETE RESTRICT ON UPDATE CASCADE,
FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang)
ON DELETE SET NULL ON UPDATE CASCADE,
FOREIGN KEY (maBan) REFERENCES Ban(maBan)
ON DELETE SET NULL ON UPDATE CASCADE,
CONSTRAINT chk_loai_don_hang
CHECK (loaiDonHang IN ('Dùng tại quán', 'Mang đi')),
CONSTRAINT chk_trang_thai_don_hang
CHECK (trangThai IN ('Đang xử lý', 'Đang phục vụ', 'Chờ thanh toán', 'Đã thanh toán', 'Đã hủy'))
);
- - =========================
-- 10. CHI TIẾT ĐƠN HÀNG
-- =========================
CREATE TABLE ChiTietDonHang (
maCTDH VARCHAR(10) PRIMARY KEY,
soLuong INT NOT NULL,
donGia DECIMAL(18,2) NOT NULL,
thanhTien DECIMAL(18,2) NOT NULL,
maDonHang VARCHAR(10) NOT NULL,
maMon VARCHAR(10) NOT NULL,
FOREIGN KEY (maDonHang) REFERENCES DonHang(maDonHang)
ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (maMon) REFERENCES Mon(maMon)
ON DELETE RESTRICT ON UPDATE CASCADE
);
- - =========================
-- 11. HÓA ĐƠN
-- =========================
CREATE TABLE HoaDon (
maHoaDon VARCHAR(10) PRIMARY KEY,
ngayThanhToan DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
phuongThucThanhToan VARCHAR(50) NOT NULL,
tongTien DECIMAL(18,2) NOT NULL,
soTienGiam DECIMAL(18,2) DEFAULT 0,
soTienPhaiTra DECIMAL(18,2) NOT NULL,
maDonHang VARCHAR(10) NOT NULL UNIQUE,
maKhuyenMai VARCHAR(10),
FOREIGN KEY (maDonHang) REFERENCES DonHang(maDonHang)
ON DELETE RESTRICT ON UPDATE CASCADE,
FOREIGN KEY (maKhuyenMai) REFERENCES KhuyenMai(maKhuyenMai)
ON DELETE SET NULL ON UPDATE CASCADE,
CONSTRAINT chk_phuong_thuc_thanh_toan
CHECK (phuongThucThanhToan IN ('Tiền mặt', 'Chuyển khoản', 'Ví điện tử'))
);
- - =========================
-- 12. NGUYÊN LIỆU
-- =========================
CREATE TABLE NguyenLieu (
maNguyenLieu VARCHAR(10) PRIMARY KEY,
tenNguyenLieu VARCHAR(100) NOT NULL,
donViTinh VARCHAR(20) NOT NULL,
soLuongTon INT NOT NULL DEFAULT 0,
mucTonToiThieu INT NOT NULL DEFAULT 0
);
- - =========================
-- 13. PHIẾU NHẬP KHO
-- =========================
CREATE TABLE PhieuNhapKho (
maPhieuNhap VARCHAR(10) PRIMARY KEY,
ngayNhap DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
ghiChu VARCHAR(255),
maNhanVien VARCHAR(10) NOT NULL,
FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien)
ON DELETE RESTRICT ON UPDATE CASCADE
);
- - =========================
-- 14. CHI TIẾT PHIẾU NHẬP
-- =========================
CREATE TABLE ChiTietPhieuNhap (
maCTPN VARCHAR(10) PRIMARY KEY,
soLuongNhap INT NOT NULL,
donGiaNhap DECIMAL(18,2) NOT NULL,
maPhieuNhap VARCHAR(10) NOT NULL,
maNguyenLieu VARCHAR(10) NOT NULL,
FOREIGN KEY (maPhieuNhap) REFERENCES PhieuNhapKho(maPhieuNhap)
ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (maNguyenLieu) REFERENCES NguyenLieu(maNguyenLieu)
ON DELETE RESTRICT ON UPDATE CASCADE
);
- - =========================
-- 15. PHIẾU XUẤT KHO
-- =========================
CREATE TABLE PhieuXuatKho (
maPhieuXuat VARCHAR(10) PRIMARY KEY,
ngayXuat DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
lyDoXuat VARCHAR(255),
maNhanVien VARCHAR(10) NOT NULL,
FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien)
ON DELETE RESTRICT ON UPDATE CASCADE
);
- - =========================
-- 16. CHI TIẾT PHIẾU XUẤT
-- =========================
CREATE TABLE ChiTietPhieuXuat (
maCTPX VARCHAR(10) PRIMARY KEY,
soLuongXuat INT NOT NULL,
maPhieuXuat VARCHAR(10) NOT NULL,
maNguyenLieu VARCHAR(10) NOT NULL,
FOREIGN KEY (maPhieuXuat) REFERENCES PhieuXuatKho(maPhieuXuat)
ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (maNguyenLieu) REFERENCES NguyenLieu(maNguyenLieu)
ON DELETE RESTRICT ON UPDATE CASCADE
);
USE coffee_management;

INSERT INTO vai_tro (ma_vai_tro, ten_vai_tro, mo_ta) VALUES
('VT01', 'ROLE_ADMIN', 'Quản trị hệ thống'),
('VT02', 'ROLE_NHANVIEN', 'Nhân viên bán hàng');

INSERT INTO nhan_vien (ma_nhan_vien, ho_ten, so_dien_thoai, dia_chi, chuc_vu, trang_thai) VALUES
('NV01', 'Quản lý', '0900000000', 'Hồ Chí Minh', 'Quản trị', 1),
('NV02', 'Nguyễn Thị Lan', '0911111111', 'Hà Nội', 'Thu ngân', 1),
('NV03', 'Trần Văn Minh', '0922222222', 'Đà Nẵng', 'Thu ngân', 1),
('NV04', 'Lê Thị Hoa', '0933333333', 'Cần Thơ', 'Thu ngân', 1),
('NV05', 'Phạm Quốc Huy', '0944444444', 'Hồ Chí Minh', 'Thu ngân', 1),
('NV06', 'Đỗ Thu Trang', '0955555555', 'Bình Dương', 'Thu ngân', 0);


INSERT INTO tai_khoan (ma_tai_khoan, ten_dang_nhap, mat_khau, trang_thai, ma_vai_tro, ma_nhan_vien) VALUES
('TK01', 'admin', '$2a$10$4zZJoW/nluXOH72..mdkcObu7upRgZo63U6gqEkhcJK8KU46FqOc2', 1, 'VT01', 'NV01'),
('TK02', 'nhanvien1', '$2a$10$4zZJoW/nluXOH72..mdkcObu7upRgZo63U6gqEkhcJK8KU46FqOc2', 1, 'VT02', 'NV02'),
('TK03', 'nhanvien2', '$2a$10$4zZJoW/nluXOH72..mdkcObu7upRgZo63U6gqEkhcJK8KU46FqOc2', 1, 'VT02', 'NV03'),
('TK04', 'nhanvien3', '$2a$10$4zZJoW/nluXOH72..mdkcObu7upRgZo63U6gqEkhcJK8KU46FqOc2', 1, 'VT02', 'NV04');

INSERT INTO ban (ma_ban, ten_ban, khu_vuc, trang_thai, ghi_chu) VALUES
('B01', 'Bàn 01', 'Tầng 1', 'Trống', 'Gần quầy'),
('B02', 'Bàn 02', 'Tầng 1', 'Trống', 'Cửa sổ'),
('B03', 'Bàn 03', 'Tầng 1', 'Đang phục vụ', 'Khách đang ngồi'),
('B04', 'Bàn 04', 'Tầng 2', 'Chờ thanh toán', 'Đang chờ thu ngân'),
('B05', 'Bàn 05', 'Tầng 2', 'Trống', 'Yên tĩnh'),
('B06', 'Bàn 06', 'Sân vườn', 'Trống', 'Ngoài trời'),
('B07', 'Bàn 07', 'Sân vườn', 'Đang phục vụ', 'Đã nhận order'),
('B08', 'Bàn 08', 'Tầng 1', 'Trống', 'Gần cửa');

INSERT INTO nhom_mon (ma_nhom_mon, ten_nhom_mon, mo_ta) VALUES
('NM01', 'Cà phê', 'Các món cà phê'),
('NM02', 'Trà', 'Các món trà'),
('NM03', 'Bánh ngọt', 'Bánh và món ăn nhẹ'),
('NM04', 'Nước ép', 'Các món nước ép trái cây'),
('NM05', 'Sinh tố', 'Các món sinh tố'),
('NM06', 'Đá xay', 'Các món đá xay');

INSERT INTO mon (ma_mon, ten_mon, don_gia, mo_ta, hinh_anh, trang_thai, ma_nhom_mon) VALUES
('M01', 'Cà phê sữa đá', 25000.00, 'Món truyền thống', NULL, 1, 'NM01'),
('M02', 'Bạc xỉu', 30000.00, 'Thơm béo, dễ uống', NULL, 1, 'NM01'),
('M03', 'Americano', 28000.00, 'Cà phê đen pha máy', NULL, 1, 'NM01'),
('M04', 'Latte', 40000.00, 'Cà phê sữa kiểu Ý', NULL, 1, 'NM01'),
('M05', 'Espresso', 22000.00, 'Cà phê đậm vị', NULL, 1, 'NM01'),
('M06', 'Trà đào cam sả', 35000.00, 'Trà trái cây mát lạnh', NULL, 1, 'NM02'),
('M07', 'Trà chanh', 20000.00, 'Trà chanh giải nhiệt', NULL, 1, 'NM02'),
('M08', 'Trà sữa matcha', 42000.00, 'Trà sữa vị matcha', NULL, 1, 'NM02'),
('M09', 'Tiramisu', 45000.00, 'Bánh ngọt kiểu Ý', NULL, 1, 'NM03'),
('M10', 'Cheesecake', 48000.00, 'Bánh phô mai mềm mịn', NULL, 1, 'NM03'),
('M11', 'Nước cam', 32000.00, 'Nước ép cam tươi', NULL, 1, 'NM04'),
('M12', 'Nước ép táo', 33000.00, 'Nước ép táo nguyên chất', NULL, 1, 'NM04'),
('M13', 'Sinh tố bơ', 38000.00, 'Sinh tố bơ sữa', NULL, 1, 'NM05'),
('M14', 'Sinh tố xoài', 38000.00, 'Sinh tố xoài sữa', NULL, 1, 'NM05'),
('M15', 'Đá xay socola', 45000.00, 'Đá xay hương socola', NULL, 1, 'NM06'),
('M16', 'Matcha Latte', 40000.00, 'Món trà xanh sữa', NULL, 0, 'NM02');

INSERT INTO khach_hang (ma_khach_hang, ho_ten, so_dien_thoai, ghi_chu) VALUES
('KH01', 'Trần Văn A', '0901234567', 'Khách quen'),
('KH02', 'Nguyễn Thị B', '0912345678', 'Thành viên mới'),
('KH03', 'Lê Văn C', NULL, 'Khách lẻ'),
('KH04', 'Phạm Minh D', '0934567890', 'Hay dùng combo'),
('KH05', 'Hoàng Thị E', '0945678901', 'Thường mua buổi sáng'),
('KH06', 'Đặng Quốc F', '0956789012', 'Khách thân thiết'),
('KH07', 'Bùi Ngọc G', '0967890123', 'Ưa thích cà phê'),
('KH08', 'Võ Thanh H', NULL, 'Khách lẻ'),
('KH09', 'Đinh Lan I', '0978901234', 'Hay đặt mang đi'),
('KH10', 'Mai Văn K', '0989012345', 'Khách gia đình');

INSERT INTO khuyen_mai (ma_khuyen_mai, ten_khuyen_mai, loai_khuyen_mai, gia_tri_giam, ngay_bat_dau, ngay_ket_thuc, trang_thai) VALUES
('KM01', 'Giảm 15k hóa đơn đầu', 'GIAM_TIEN', 15000.00, DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), 1),
('KM02', 'Giảm 10% cuối tuần', 'GIAM_PHAN_TRAM', 10.00, DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), 1),
('KM03', 'Giảm 20k cho đơn lớn', 'GIAM_TIEN', 20000.00, DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_ADD(NOW(), INTERVAL 15 DAY), 1),
('KM04', 'Giảm 5% khách mới', 'GIAM_PHAN_TRAM', 5.00, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_ADD(NOW(), INTERVAL 20 DAY), 1),
('KM05', 'Khuyến mãi hết hạn', 'GIAM_TIEN', 25000.00, DATE_SUB(NOW(), INTERVAL 60 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), 0),
('KM06', 'Giảm 30k cuối tháng', 'GIAM_TIEN', 30000.00, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 10 DAY), 1);

INSERT INTO nguyen_lieu (ma_nguyen_lieu, ten_nguyen_lieu, don_vi_tinh, so_luong_ton, muc_ton_toi_thieu) VALUES
('NL01', 'Sữa tươi', 'Hộp', 30, 10),
('NL02', 'Cà phê bột', 'Kg', 12, 5),
('NL03', 'Đường', 'Kg', 8, 6),
('NL04', 'Trân châu', 'Kg', 3, 5),
('NL05', 'Bột matcha', 'Kg', 7, 3),
('NL06', 'Cam tươi', 'Kg', 15, 8),
('NL07', 'Táo tươi', 'Kg', 14, 8),
('NL08', 'Bơ tươi', 'Kg', 6, 4),
('NL09', 'Socola bột', 'Kg', 9, 4),
('NL10', 'Kem béo', 'Lít', 5, 3),
('NL11', 'Syrup đào', 'Chai', 4, 2),
('NL12', 'Bột trà đen', 'Kg', 10, 5);

INSERT INTO phieu_nhap_kho (ma_phieu_nhap, ngay_nhap, ghi_chu, ma_nhan_vien) VALUES
('PN001', DATE_SUB(NOW(), INTERVAL 20 DAY), 'Nhập nguyên liệu đầu tháng', 'NV01'),
('PN002', DATE_SUB(NOW(), INTERVAL 10 DAY), 'Bổ sung nguyên liệu bán chạy', 'NV01'),
('PN003', DATE_SUB(NOW(), INTERVAL 3 DAY), 'Nhập thêm phục vụ cuối tuần', 'NV02'),
('PN004', DATE_SUB(NOW(), INTERVAL 1 DAY), 'Nhập gấp nguyên liệu', 'NV01');

INSERT INTO chi_tiet_phieu_nhap (ma_ctpn, so_luong_nhap, don_gia_nhap, ma_phieu_nhap, ma_nguyen_lieu) VALUES
('CTPN001', 10, 120000.00, 'PN001', 'NL01'),
('CTPN002', 5, 180000.00, 'PN001', 'NL02'),
('CTPN003', 8, 150000.00, 'PN001', 'NL03'),
('CTPN004', 6, 130000.00, 'PN002', 'NL04'),
('CTPN005', 4, 220000.00, 'PN002', 'NL05'),
('CTPN006', 12, 100000.00, 'PN003', 'NL06'),
('CTPN007', 12, 100000.00, 'PN003', 'NL07'),
('CTPN008', 8, 140000.00, 'PN003', 'NL08'),
('CTPN009', 7, 160000.00, 'PN004', 'NL09'),
('CTPN010', 3, 250000.00, 'PN004', 'NL10');

INSERT INTO phieu_xuat_kho (ma_phieu_xuat, ngay_xuat, ly_do_xuat, ma_nhan_vien) VALUES
('PX001', DATE_SUB(NOW(), INTERVAL 7 DAY), 'Hủy nguyên liệu hỏng', 'NV01'),
('PX002', DATE_SUB(NOW(), INTERVAL 2 DAY), 'Xuất cho quầy pha chế', 'NV02'),
('PX003', NOW(), 'Kiểm kê điều chỉnh', 'NV01');

INSERT INTO chi_tiet_phieu_xuat (ma_ctpx, so_luong_xuat, ma_phieu_xuat, ma_nguyen_lieu) VALUES
('CTPX001', 2, 'PX001', 'NL04'),
('CTPX002', 1, 'PX001', 'NL08'),
('CTPX003', 3, 'PX002', 'NL01'),
('CTPX004', 2, 'PX002', 'NL02'),
('CTPX005', 1, 'PX002', 'NL06'),
('CTPX006', 1, 'PX003', 'NL10');

INSERT INTO don_hang (ma_don_hang, ngay_dat, loai_don_hang, trang_thai, tong_tien, ma_nhan_vien, ma_khach_hang, ma_ban) VALUES
('DH001', DATE_SUB(NOW(), INTERVAL 5 DAY), 'Dùng tại quán', 'Đã thanh toán', 55000.00, 'NV01', 'KH01', 'B01'),
('DH002', DATE_SUB(NOW(), INTERVAL 4 DAY), 'Mang đi', 'Đã thanh toán', 35000.00, 'NV02', 'KH02', NULL),
('DH003', DATE_SUB(NOW(), INTERVAL 3 DAY), 'Dùng tại quán', 'Chờ thanh toán', 78000.00, 'NV03', 'KH03', 'B03'),
('DH004', DATE_SUB(NOW(), INTERVAL 2 DAY), 'Mang đi', 'Đang xử lý', 82000.00, 'NV02', 'KH04', NULL),
('DH005', DATE_SUB(NOW(), INTERVAL 1 DAY), 'Dùng tại quán', 'Đã thanh toán', 105000.00, 'NV01', 'KH05', 'B04'),
('DH006', NOW(), 'Mang đi', 'Đang xử lý', 68000.00, 'NV04', 'KH06', NULL),
('DH007', NOW(), 'Dùng tại quán', 'Đang phục vụ', 92000.00, 'NV03', 'KH07', 'B07'),
('DH008', NOW(), 'Mang đi', 'Chờ thanh toán', 45000.00, 'NV05', 'KH08', NULL);

INSERT INTO chi_tiet_don_hang (ma_ctdh, so_luong, don_gia, thanh_tien, ma_don_hang, ma_mon) VALUES
('CTDH001', 1, 25000.00, 25000.00, 'DH001', 'M01'),
('CTDH002', 1, 30000.00, 30000.00, 'DH001', 'M02'),
('CTDH003', 1, 35000.00, 35000.00, 'DH002', 'M06'),
('CTDH004', 2, 20000.00, 40000.00, 'DH003', 'M07'),
('CTDH005', 1, 38000.00, 38000.00, 'DH003', 'M13'),
('CTDH006', 2, 41000.00, 82000.00, 'DH004', 'M08'),
('CTDH007', 2, 45000.00, 90000.00, 'DH005', 'M09'),
('CTDH008', 1, 15000.00, 15000.00, 'DH005', 'M01'),
('CTDH009', 1, 28000.00, 28000.00, 'DH006', 'M03'),
('CTDH010', 1, 40000.00, 40000.00, 'DH006', 'M04'),
('CTDH011', 2, 46000.00, 92000.00, 'DH007', 'M10'),
('CTDH012', 1, 45000.00, 45000.00, 'DH008', 'M15');

INSERT INTO hoa_don (ma_hoa_don, ngay_thanh_toan, phuong_thuc_thanh_toan, tong_tien, so_tien_giam, so_tien_phai_tra, ma_don_hang, ma_khuyen_mai) VALUES
('HD001', DATE_SUB(NOW(), INTERVAL 5 DAY), 'Tiền mặt', 55000.00, 15000.00, 40000.00, 'DH001', 'KM01'),
('HD002', DATE_SUB(NOW(), INTERVAL 4 DAY), 'Chuyển khoản', 35000.00, 3500.00, 31500.00, 'DH002', 'KM02'),
('HD003', DATE_SUB(NOW(), INTERVAL 1 DAY), 'Ví điện tử', 105000.00, 20000.00, 85000.00, 'DH005', 'KM03');
