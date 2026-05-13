# Coffee Management System - Hệ thống Quản lý Cà phê Hoàng Tuấn

## 1. Giới thiệu dự án

**Coffee Management System - Hệ thống Quản lý Cà phê Hoàng Tuấn** là một ứng dụng web quản lý quán cà phê được xây dựng theo kiến trúc **Java Spring Boot MVC**. Dự án tập trung vào các nghiệp vụ cốt lõi trong vận hành quán cà phê như đăng nhập, phân quyền cố định, quản lý menu, bán hàng, thanh toán, quản lý kho và thống kê báo cáo.

Hệ thống được thiết kế theo tư duy **clean code**, dễ đọc, dễ mở rộng và phù hợp cho đồ án sinh viên nhưng vẫn bám sát thực tế triển khai.

### Mục tiêu chính
- Quản lý hoạt động bán hàng tại quán và mang đi.
- Quản lý bàn, đơn hàng, hóa đơn, khách hàng và khuyến mãi.
- Quản lý nhân sự, tài khoản và vai trò cố định.
- Quản lý nguyên liệu, nhập kho, xuất kho và theo dõi tồn kho.
- Hỗ trợ báo cáo thống kê phục vụ quản trị.

---

## 2. Công nghệ sử dụng

| Công nghệ | Vai trò |
|---|---|
| Java | Ngôn ngữ lập trình chính |
| Spring Boot | Framework nền tảng cho toàn bộ ứng dụng |
| Spring MVC | Xây dựng mô hình xử lý request và controller |
| Spring Data JPA | Truy xuất dữ liệu theo hướng repository |
| Hibernate | ORM ánh xạ Entity với database |
| MySQL | Hệ quản trị cơ sở dữ liệu |
| Thymeleaf | Render giao diện phía server |
| Spring Security | Đăng nhập, xác thực và phân quyền |
| BCrypt | Mã hóa mật khẩu người dùng |
| Maven | Quản lý phụ thuộc và build project |
| Bootstrap/CSS/JavaScript | Xây dựng giao diện và tương tác |

---

## 3. Kiến trúc tổng quan

Hệ thống được xây dựng theo mô hình phân lớp:

`Browser -> Controller -> Service -> Repository -> Entity -> Database`

### Vai trò từng lớp
- **Controller**: Nhận request từ trình duyệt, điều hướng sang service và trả về view hoặc redirect.
- **Service**: Xử lý nghiệp vụ chính, kiểm tra điều kiện, điều phối luồng dữ liệu.
- **Repository**: Thao tác trực tiếp với database bằng Spring Data JPA.
- **Entity**: Ánh xạ bảng trong MySQL sang object Java.
- **DTO**: Truyền dữ liệu trung gian cho form, dashboard, báo cáo hoặc dữ liệu tổng hợp.
- **Security**: Xử lý đăng nhập, mã hóa mật khẩu, phân quyền theo vai trò cố định.
- **View**: Giao diện Thymeleaf hiển thị dữ liệu cho người dùng.

### Vì sao kiến trúc này dễ mở rộng
- Tách rõ trách nhiệm giữa các lớp.
- Dễ bảo trì, dễ test và dễ thay đổi logic mà không ảnh hưởng toàn hệ thống.
- Có thể thêm module mới như nhà cung cấp, khuyến mãi nâng cao hoặc báo cáo mới mà vẫn giữ cấu trúc cũ.

---

## 4. Cấu trúc thư mục dự án

```text
src/main/java/com/hoangtuan/coffeemanagement
├── config
├── controller
├── dto
├── entity
├── exception
├── repository
├── security
├── service
├── service/impl
└── util

src/main/resources
├── application.properties
├── static
│   ├── css
│   │   └── style.css
│   ├── js
│   │   └── main.js
│   └── images
├── templates
│   ├── layout
│   │   └── main.html
│   ├── auth
│   │   └── login.html
│   ├── dashboard
│   │   └── index.html
│   ├── nhanvien
│   ├── vaitro
│   ├── taikhoan
│   ├── nhommon
│   ├── mon
│   ├── ban
│   ├── banhang
│   ├── donhang
│   ├── hoadon
│   ├── khachhang
│   ├── khuyenmai
│   ├── nguyenlieu
│   ├── phieunhapkho
│   ├── phieuxuatkho
│   ├── baocao
│   └── error
│       └── 403.html
└── database
    └── coffee_management_seed.sql
```

---

## 5. Mô tả các package

| Package | Nhiệm vụ |
|---|---|
| `config` | Cấu hình Spring Boot, JPA, Thymeleaf, MVC và các bean dùng chung |
| `controller` | Nhận request, validate cơ bản, gọi service và trả view |
| `entity` | Định nghĩa mapping giữa bảng MySQL và object Java |
| `repository` | Tầng truy vấn dữ liệu, kế thừa `JpaRepository` |
| `service` | Khai báo nghiệp vụ dưới dạng interface |
| `service.impl` | Cài đặt logic nghiệp vụ cụ thể |
| `security` | Đăng nhập, phân quyền, custom user details, success handler |
| `dto` | Đối tượng trung gian cho form, dashboard, báo cáo, thống kê |
| `exception` | Xử lý lỗi nghiệp vụ và lỗi hệ thống theo cách thống nhất |
| `util` | Hằng số, helper, format dữ liệu, sinh mã code nếu cần |

---

## 6. Database

- **Tên database**: `coffee_management`
- **Quy tắc đặt tên bảng/cột**: dùng `snake_case`
- **Quy tắc Entity Java**: dùng `camelCase`
- **Khóa chính**: dùng `String`, ví dụ `NV01`, `M01`, `DH01`, `HD01`
- **Không dùng**: `Long id`, `@GeneratedValue`
- **Khuyến mãi**: chỉ áp dụng ở bảng `hoa_don`, không đưa vào `don_hang`

### Danh sách bảng chính
- `vai_tro`
- `tai_khoan`
- `nhan_vien`
- `ban`
- `khach_hang`
- `khuyen_mai`
- `nhom_mon`
- `mon`
- `don_hang`
- `chi_tiet_don_hang`
- `hoa_don`
- `nguyen_lieu`
- `phieu_nhap_kho`
- `chi_tiet_phieu_nhap`
- `phieu_xuat_kho`
- `chi_tiet_phieu_xuat`

---

## 7. Vai trò và phân quyền

Hệ thống chỉ sử dụng **2 vai trò cố định**:

| Vai trò | Quyền truy cập |
|---|---|
| `ROLE_ADMIN` | Truy cập toàn bộ hệ thống |
| `ROLE_NHANVIEN` | Chỉ truy cập các chức năng bán hàng và vận hành được phân quyền |

### Nhóm chức năng cho từng vai trò

| Nhóm chức năng | ROLE_ADMIN | ROLE_NHANVIEN |
|---|---:|---:|
| Đăng nhập / đăng xuất | Có | Có |
| Dashboard | Có | Không |
| Quản lý nhân viên | Có | Không |
| Quản lý vai trò | Có | Không |
| Quản lý tài khoản | Có | Không |
| Quản lý nhóm món | Có | Không |
| Quản lý món/menu | Có | Không |
| Quản lý bàn | Có | Có |
| Bán hàng | Có | Có |
| Đơn hàng | Có | Có |
| Hóa đơn | Có | Có |
| Khách hàng | Có | Có |
| Khuyến mãi | Có | Không |
| Kho nguyên liệu | Có | Không |
| Nhập kho | Có | Không |
| Xuất kho | Có | Không |
| Báo cáo thống kê | Có | Không |
| Xem thông tin cá nhân | Có | Có |
| Cập nhật thông tin cá nhân | Có | Có |
| Đổi mật khẩu cá nhân | Có | Có |
| Tạo tài khoản nhân viên | Có | Không |
| Reset mật khẩu nhân viên | Có | Không |
| Khóa / mở tài khoản | Có | Không |
| Gán vai trò tài khoản | Có | Không |
| Cập nhật thông tin nhân viên | Có | Không |

> Lưu ý: dự án **không dùng phân quyền động**, **không có bảng `quyen`**, **không có bảng `vai_tro_quyen`**.

---

## 8. Chức năng chính

### 1. Đăng nhập
- Người dùng đăng nhập bằng tài khoản và mật khẩu.
- Mật khẩu được mã hóa bằng BCrypt.
- Sau khi đăng nhập, hệ thống điều hướng theo role.

### 2. Quản lý bàn
- Xem danh sách bàn.
- Cập nhật trạng thái bàn.
- Hỗ trợ bàn trống, đang phục vụ và chờ thanh toán.

### 3. Bán hàng
- Tạo đơn trực tiếp tại quán hoặc mang đi.
- Thêm món, sửa số lượng, cập nhật tổng tiền.

### 4. Đơn dùng tại quán
- Chọn bàn trống.
- Gắn đơn hàng với bàn.
- Cập nhật trạng thái bàn trong quá trình phục vụ.

### 5. Đơn mang đi
- Tạo đơn không cần chọn bàn.
- Phù hợp cho khách mua mang đi nhanh.

### 6. Thanh toán hóa đơn
- Tính tổng tiền, số tiền giảm và số tiền phải trả.
- In hóa đơn sau khi thanh toán.

### 7. Áp dụng khuyến mãi
- Chọn khuyến mãi còn hiệu lực.
- Khuyến mãi được áp dụng ở `hoa_don`.

### 8. Quản lý menu
- Quản lý nhóm món và món.
- Tra cứu menu theo tên hoặc nhóm món.

### 9. Quản lý khách hàng
- Lưu thông tin khách hàng.
- Tra cứu khách hàng phục vụ bán hàng và chăm sóc sau mua.

### 10. Quản lý kho
- Quản lý nguyên liệu.
- Nhập kho và xuất kho.
- Theo dõi tồn kho và cảnh báo sắp hết.

### 11. Báo cáo thống kê
- Báo cáo doanh thu.
- Báo cáo đơn hàng.
- Báo cáo nhập xuất kho nếu cần.

### 12. Quản lý tài khoản và thông tin cá nhân

#### Với Admin
- Tạo tài khoản đăng nhập cho nhân viên.
- Gán vai trò cố định cho tài khoản: `ROLE_ADMIN` hoặc `ROLE_NHANVIEN`.
- Cập nhật trạng thái tài khoản: hoạt động hoặc bị khóa.
- Reset mật khẩu cho nhân viên khi cần.
- Cập nhật thông tin nhân viên như họ tên, số điện thoại, địa chỉ, chức vụ và trạng thái làm việc.

#### Với Nhân viên bán hàng
- Xem thông tin cá nhân của mình.
- Cập nhật một số thông tin cá nhân cơ bản nếu hệ thống cho phép.
- Đổi mật khẩu đăng nhập của chính mình.
- Không được tạo tài khoản cho người khác.
- Không được thay đổi vai trò hoặc trạng thái tài khoản.
- Không được reset mật khẩu cho người khác.

---

## 9. Luồng nghiệp vụ quan trọng

### Đăng nhập
1. Người dùng mở trang đăng nhập.
2. Nhập tên đăng nhập và mật khẩu.
3. Spring Security kiểm tra tài khoản.
4. BCrypt so khớp mật khẩu.
5. Hệ thống điều hướng theo vai trò.

### Tạo đơn dùng tại quán
1. Nhân viên tạo đơn mới.
2. Chọn bàn trống.
3. Chọn món và số lượng.
4. Lưu đơn hàng.
5. Cập nhật trạng thái bàn sang đang phục vụ.

### Tạo đơn mang đi
1. Nhân viên tạo đơn mới.
2. Chọn loại đơn là mang đi.
3. Chọn món và số lượng.
4. Lưu đơn hàng mà không cần chọn bàn.

### Thanh toán hóa đơn
1. Chọn đơn hàng cần thanh toán.
2. Hệ thống tính tổng tiền.
3. Chọn khuyến mãi nếu có.
4. Tính số tiền giảm và số tiền phải trả.
5. Tạo hóa đơn.
6. Đánh dấu đơn hàng đã thanh toán.
7. Nếu là đơn tại quán thì trả bàn về trạng thái trống.

### Nhập kho
1. Chọn nguyên liệu.
2. Nhập số lượng và đơn giá.
3. Lưu phiếu nhập kho.
4. Tăng tồn kho.

### Xuất kho
1. Chọn nguyên liệu cần xuất.
2. Nhập số lượng xuất.
3. Lưu phiếu xuất kho.
4. Giảm tồn kho.

### Báo cáo doanh thu
1. Chọn khoảng thời gian thống kê.
2. Lọc hóa đơn đã thanh toán.
3. Tổng hợp doanh thu.
4. Hiển thị bảng hoặc biểu đồ.

### Tạo tài khoản cho nhân viên
1. Admin đăng nhập vào hệ thống.
2. Admin vào chức năng quản lý tài khoản.
3. Chọn nhân viên chưa có tài khoản.
4. Nhập tên đăng nhập, mật khẩu ban đầu và vai trò.
5. Hệ thống mã hóa mật khẩu bằng BCrypt.
6. Hệ thống lưu tài khoản vào bảng `tai_khoan`.
7. Nhân viên có thể đăng nhập bằng tài khoản được cấp.

### Reset mật khẩu nhân viên
1. Admin vào danh sách tài khoản.
2. Chọn tài khoản cần reset mật khẩu.
3. Nhập mật khẩu mới hoặc dùng mật khẩu mặc định.
4. Hệ thống mã hóa mật khẩu mới bằng BCrypt.
5. Hệ thống cập nhật trường `mat_khau` trong bảng `tai_khoan`.
6. Nhân viên đăng nhập lại bằng mật khẩu mới.

### Đổi mật khẩu cá nhân
1. Người dùng đăng nhập vào hệ thống.
2. Người dùng mở trang thông tin cá nhân.
3. Chọn chức năng đổi mật khẩu.
4. Nhập mật khẩu hiện tại, mật khẩu mới và xác nhận mật khẩu mới.
5. Hệ thống kiểm tra mật khẩu hiện tại bằng BCrypt.
6. Nếu đúng, hệ thống mã hóa mật khẩu mới bằng BCrypt và cập nhật vào bảng `tai_khoan`.
7. Nếu sai, hệ thống hiển thị thông báo lỗi.

---

## 10. Cấu hình `application.properties`

```properties
spring.application.name=coffee-management
server.port=8080

spring.datasource.url=jdbc:mysql://localhost:3306/coffee_management?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=Asia/Ho_Chi_Minh&allowPublicKeyRetrieval=true&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.thymeleaf.cache=false
spring.thymeleaf.encoding=UTF-8

server.servlet.encoding.charset=UTF-8
server.servlet.encoding.enabled=true
server.servlet.encoding.force=true
```

### Giải thích `ddl-auto`
- `create`: tạo lại bảng từ đầu, phù hợp lúc thử nghiệm ban đầu.
- `validate`: kiểm tra schema có khớp entity hay không, phù hợp khi database đã ổn định.
- `none`: không để Hibernate tự kiểm tra hay thay đổi schema.

> Không nên dùng `create` lâu dài vì có thể làm mất dữ liệu.

---

## 11. Cách chạy dự án

### Bước 1: Clone project
```bash
git clone <url-repository>
cd coffee_management
```

### Bước 2: Tạo database
- Để `createDatabaseIfNotExist=true` tự tạo khi chạy lần đầu.

### Bước 3: Import SQL seed
- Import file `src/main/resources/database/coffee_management_seed.sql` hoặc file SQL seed mà dự án đang dùng.

### Bước 4: Cấu hình kết nối
- Mở `application.properties`
- Sửa `username`, `password` theo máy của bạn

### Bước 5: Chạy ứng dụng
```bash
mvn spring-boot:run
```

Hoặc chạy trực tiếp class:
- `CoffeeManagementApplication.java`

### Bước 6: Truy cập ứng dụng
```text
http://localhost:8080
```

---

## 12. Tài khoản mẫu

| Tài khoản | Mật khẩu | Vai trò |
|---|---|---|
| `admin` | `admin` | `ROLE_ADMIN` |
| `nhanvien` | `admin` | `ROLE_NHANVIEN` |

> Mật khẩu thực tế trong database phải được mã hóa bằng BCrypt.

---

## 13. Route chính

| Route | Chức năng | Quyền |
|---|---|---|
| `/login` | Đăng nhập | Tất cả người dùng |
| `/dashboard` | Dashboard | `ROLE_ADMIN` |
| `/nhan-vien/**` | Quản lý nhân viên | `ROLE_ADMIN` |
| `/vai-tro/**` | Quản lý vai trò | `ROLE_ADMIN` |
| `/tai-khoan/**` | Quản lý tài khoản | `ROLE_ADMIN` |
| `/tai-khoan/them` | Tạo tài khoản nhân viên | `ROLE_ADMIN` |
| `/tai-khoan/sua/**` | Cập nhật tài khoản | `ROLE_ADMIN` |
| `/tai-khoan/khoa/**` | Khóa / mở tài khoản | `ROLE_ADMIN` |
| `/tai-khoan/reset-mat-khau/**` | Reset mật khẩu nhân viên | `ROLE_ADMIN` |
| `/ca-nhan` | Xem thông tin cá nhân | `ROLE_ADMIN`, `ROLE_NHANVIEN` |
| `/ca-nhan/cap-nhat` | Cập nhật thông tin cá nhân | `ROLE_ADMIN`, `ROLE_NHANVIEN` |
| `/ca-nhan/doi-mat-khau` | Đổi mật khẩu cá nhân | `ROLE_ADMIN`, `ROLE_NHANVIEN` |
| `/nhom-mon/**` | Quản lý nhóm món | `ROLE_ADMIN` |
| `/mon/**` | Quản lý món | `ROLE_ADMIN` |
| `/ban/**` | Quản lý bàn | `ROLE_ADMIN`, `ROLE_NHANVIEN` |
| `/ban-hang/**` | Bán hàng | `ROLE_ADMIN`, `ROLE_NHANVIEN` |
| `/don-hang/**` | Đơn hàng | `ROLE_ADMIN`, `ROLE_NHANVIEN` |
| `/hoa-don/**` | Hóa đơn | `ROLE_ADMIN`, `ROLE_NHANVIEN` |
| `/khach-hang/**` | Khách hàng | `ROLE_ADMIN`, `ROLE_NHANVIEN` |
| `/khuyen-mai/**` | Khuyến mãi | `ROLE_ADMIN` |
| `/nguyen-lieu/**` | Kho nguyên liệu | `ROLE_ADMIN` |
| `/phieu-nhap-kho/**` | Nhập kho | `ROLE_ADMIN` |
| `/phieu-xuat-kho/**` | Xuất kho | `ROLE_ADMIN` |
| `/bao-cao/**` | Báo cáo thống kê | `ROLE_ADMIN` |
| `/logout` | Đăng xuất | Tất cả người dùng đã đăng nhập |

---

## 14. Kiểm thử nhanh

### Kịch bản kiểm thử đề xuất
- Admin đăng nhập và thấy toàn bộ menu.
- Nhân viên đăng nhập và chỉ thấy các menu được phép.
- Nhân viên truy cập `/nhan-vien` và nhận `403 Forbidden`.
- Admin tạo tài khoản cho nhân viên mới.
- Admin reset mật khẩu cho nhân viên.
- Admin khóa tài khoản nhân viên và nhân viên không đăng nhập được.
- Admin mở khóa tài khoản nhân viên và nhân viên đăng nhập lại được.
- Nhân viên đổi mật khẩu cá nhân thành công.
- Nhân viên nhập sai mật khẩu hiện tại khi đổi mật khẩu thì hệ thống báo lỗi.
- Nhân viên không truy cập được `/tai-khoan`.
- Nhân viên chỉ xem hoặc sửa được thông tin cá nhân của chính mình.
- Tạo đơn dùng tại quán.
- Tạo đơn mang đi.
- Thanh toán đơn tại bàn.
- Thanh toán đơn mang đi.
- Nhập kho.
- Xuất kho.
- Xem báo cáo doanh thu.

---

## 15. Quy tắc clean code

- `Controller` không xử lý nghiệp vụ phức tạp.
- `Service` chịu trách nhiệm xử lý logic.
- `Repository` chỉ truy vấn database.
- Không gọi `Repository` trực tiếp từ `View`.
- Không hard-code role ở nhiều nơi.
- Dùng `DTO` cho dashboard và báo cáo.
- Entity phải khớp đúng database.
- Không thêm field ngoài database.
- Không dùng `Long id`.
- Không dùng `@GeneratedValue`.
- Logic đổi mật khẩu và reset mật khẩu phải đặt trong `TaiKhoanService`, không xử lý trực tiếp trong `Controller`.
- Không lưu mật khẩu dạng plain text.
- Tất cả mật khẩu phải được mã hóa bằng BCrypt trước khi lưu database.
- Người dùng thường chỉ được cập nhật thông tin cá nhân của chính mình.
- Chỉ Admin mới được tạo tài khoản, khóa tài khoản, reset mật khẩu và gán vai trò.
- Controller chỉ nhận request, validate cơ bản và gọi service.
- Service chịu trách nhiệm kiểm tra quyền nghiệp vụ và xử lý đổi mật khẩu.

---

## 16. Hướng dẫn mở rộng chức năng

Nếu muốn thêm một module mới, ví dụ `NhaCungCap`, quy trình nên là:

1. Tạo `Entity`
2. Tạo `Repository`
3. Tạo `Service`
4. Tạo `ServiceImpl`
5. Tạo `Controller`
6. Tạo `View`
7. Thêm menu nếu cần
8. Cấu hình quyền truy cập nếu module đó chỉ dành cho admin

Nguyên tắc là giữ nguyên kiến trúc phân lớp để hệ thống không bị rối khi mở rộng.

---

## 17. Lỗi thường gặp

### 403 Forbidden
- Nguyên nhân: user không đủ quyền truy cập URL.
- Cách xử lý: kiểm tra role và cấu hình `SecurityConfig`.

### Không nhận CSS
- Nguyên nhân: đường dẫn static sai hoặc file chưa nằm đúng thư mục `static`.
- Cách xử lý: kiểm tra `src/main/resources/static/css/style.css`.

### Lỗi font tiếng Việt
- Nguyên nhân: encoding chưa đúng.
- Cách xử lý: đảm bảo UTF-8 ở `application.properties`, file HTML, và database.

### `Unknown column`
- Nguyên nhân: Entity có field không tồn tại trong database.
- Cách xử lý: sửa Entity cho khớp đúng schema hiện tại.

### `Table doesn't exist`
- Nguyên nhân: chưa import SQL seed hoặc đang trỏ sai database.
- Cách xử lý: kiểm tra tên database và import đúng file SQL.

### Role không khớp
- Nguyên nhân: tên role trong database khác với tên cấu hình Spring Security.
- Cách xử lý: giữ đúng `ROLE_ADMIN` và `ROLE_NHANVIEN`.

### BCrypt không đăng nhập được
- Nguyên nhân: mật khẩu trong database không được mã hóa đúng kiểu BCrypt.
- Cách xử lý: tạo lại mật khẩu bằng BCrypt và lưu vào database.

---

## 18. Kết luận

Dự án **Coffee Management System - Hệ thống Quản lý Cà phê Hoàng Tuấn** được thiết kế theo hướng rõ ràng, phân lớp chặt chẽ, phân quyền cố định đơn giản nhưng thực tế. Kiến trúc này phù hợp cho đồ án sinh viên, dễ thuyết trình, dễ kiểm thử và có thể mở rộng thêm nhiều chức năng sau này mà vẫn giữ được cấu trúc clean code.

Nếu triển khai đúng theo README này, bạn sẽ có một nền tảng dự án vừa dễ đọc, vừa dễ bảo trì, vừa phù hợp để phát triển tiếp thành hệ thống thực tế.
