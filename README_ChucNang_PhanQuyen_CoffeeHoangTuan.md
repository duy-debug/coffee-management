# README - Chức năng hệ thống Quản lý Cà phê Hoàng Tuấn

## 1. Giới thiệu

Tài liệu này mô tả các chức năng chính của hệ thống Quản lý Cà phê Hoàng Tuấn, kèm theo vai trò được phép sử dụng và kịch bản xử lý của từng chức năng.

Hệ thống sử dụng cơ chế phân quyền cố định theo vai trò. Mỗi tài khoản được gán một vai trò cụ thể. Quyền sử dụng chức năng được xác định dựa trên vai trò đó.

## 2. Vai trò người dùng

### 2.1 Admin

Admin là người quản trị hệ thống, có quyền sử dụng toàn bộ chức năng của phần mềm.

Admin được phép:

- Đăng nhập hệ thống.
- Xác định vai trò người dùng.
- Tra cứu và cập nhật thông tin nhân viên.
- Tạo và chỉnh sửa đơn hàng.
- Chọn bàn và cập nhật trạng thái bàn.
- Thanh toán và in hóa đơn.
- Tra cứu menu.
- Cập nhật thông tin món.
- Cập nhật kho nguyên liệu.
- Cập nhật thông tin khách hàng.
- Cập nhật chương trình khuyến mãi.
- Áp dụng khuyến mãi vào hóa đơn.
- Xem báo cáo thống kê.

### 2.2 Thu ngân

Thu ngân là người trực tiếp thực hiện các nghiệp vụ thu ngân tại quán.

Thu ngân được phép:

- Đăng nhập hệ thống.
- Xác định vai trò người dùng.
- Tạo đơn hàng.
- Chỉnh sửa đơn hàng.
- Chọn bàn.
- Cập nhật trạng thái bàn.
- Thanh toán và in hóa đơn.
- Tra cứu menu.
- Cập nhật thông tin khách hàng cơ bản.
- Áp dụng khuyến mãi vào hóa đơn.

Thu ngân không được phép:

- Tra cứu và cập nhật thông tin nhân viên.
- Cập nhật thông tin món.
- Cập nhật kho nguyên liệu.
- Cập nhật chương trình khuyến mãi.
- Xem báo cáo thống kê.

## 3. Danh sách chức năng và phân quyền

| STT | Chức năng | Admin | Thu ngân |
| --- | --- | --- | --- |
| 1 | Đăng nhập | Có | Có |
| 2 | Xác định vai trò người dùng | Có | Có |
| 3 | Tra cứu thông tin nhân viên | Có | Không |
| 4 | Cập nhật thông tin nhân viên | Có | Không |
| 5 | Tạo đơn hàng | Có | Có |
| 6 | Chỉnh sửa đơn hàng | Có | Có |
| 7 | Chọn bàn | Có | Có |
| 8 | Cập nhật trạng thái bàn | Có | Có |
| 9 | Thanh toán và in hóa đơn | Có | Có |
| 10 | Tra cứu menu | Có | Có |
| 11 | Cập nhật thông tin món | Có | Không |
| 12 | Cập nhật kho nguyên liệu | Có | Không |
| 13 | Cập nhật thông tin khách hàng | Có | Có |
| 14 | Cập nhật chương trình khuyến mãi | Có | Không |
| 15 | Áp dụng khuyến mãi vào hóa đơn | Có | Có |
| 16 | Xem báo cáo thống kê | Có | Không |

## 4. Kịch bản chức năng

### 4.1 Đăng nhập

**Vai trò được sử dụng:** Admin, Thu ngân

**Mục tiêu:** Cho phép người dùng truy cập vào hệ thống bằng tài khoản đã được cấp.

**Kịch bản chính:**

1. Người dùng mở màn hình đăng nhập.
2. Người dùng nhập tên đăng nhập và mật khẩu.
3. Hệ thống kiểm tra thông tin tài khoản.
4. Nếu thông tin hợp lệ, hệ thống cho phép người dùng đăng nhập.
5. Hệ thống chuyển người dùng đến giao diện phù hợp với vai trò.

**Kịch bản ngoại lệ:**

- Nếu tên đăng nhập hoặc mật khẩu không đúng, hệ thống hiển thị thông báo lỗi.
- Nếu tài khoản bị khóa, hệ thống từ chối đăng nhập.

---

### 4.2 Xác định vai trò người dùng

**Vai trò được sử dụng:** Admin, Thu ngân

**Mục tiêu:** Xác định quyền truy cập của người dùng sau khi đăng nhập.

**Kịch bản chính:**

1. Người dùng đăng nhập thành công.
2. Hệ thống lấy thông tin vai trò của tài khoản.
3. Nếu vai trò là Admin, hệ thống hiển thị toàn bộ chức năng.
4. Nếu vai trò là Thu ngân, hệ thống chỉ hiển thị các chức năng bán hàng được phép sử dụng.

**Kịch bản ngoại lệ:**

- Nếu tài khoản chưa được gán vai trò, hệ thống không cho truy cập chức năng chính.

---

### 4.3 Tra cứu thông tin nhân viên

**Vai trò được sử dụng:** Admin

**Mục tiêu:** Cho phép Admin xem danh sách và thông tin chi tiết của nhân viên.

**Kịch bản chính:**

1. Admin mở chức năng tra cứu thông tin nhân viên.
2. Hệ thống hiển thị danh sách nhân viên.
3. Admin nhập từ khóa tìm kiếm như tên, số điện thoại hoặc chức vụ.
4. Hệ thống lọc và hiển thị kết quả phù hợp.
5. Admin chọn một nhân viên để xem thông tin chi tiết.

**Kịch bản ngoại lệ:**

- Nếu không tìm thấy dữ liệu phù hợp, hệ thống hiển thị thông báo không có kết quả.

---

### 4.4 Cập nhật thông tin nhân viên

**Vai trò được sử dụng:** Admin

**Mục tiêu:** Cho phép Admin thêm mới hoặc chỉnh sửa thông tin nhân viên.

**Kịch bản chính:**

1. Admin mở chức năng cập nhật thông tin nhân viên.
2. Admin chọn thêm mới hoặc chỉnh sửa nhân viên.
3. Admin nhập hoặc điều chỉnh thông tin nhân viên.
4. Hệ thống kiểm tra dữ liệu đầu vào.
5. Nếu dữ liệu hợp lệ, hệ thống lưu thông tin vào cơ sở dữ liệu.
6. Hệ thống hiển thị thông báo cập nhật thành công.

**Kịch bản ngoại lệ:**

- Nếu thiếu thông tin bắt buộc, hệ thống yêu cầu nhập đầy đủ.
- Nếu số điện thoại không hợp lệ, hệ thống hiển thị thông báo lỗi.

---

### 4.5 Tạo đơn hàng

**Vai trò được sử dụng:** Admin, Thu ngân

**Mục tiêu:** Tạo đơn hàng cho khách dùng tại quán hoặc khách mang đi.

**Kịch bản chính:**

1. Người dùng mở màn hình tạo đơn hàng.
2. Người dùng chọn loại đơn hàng: Dùng tại quán hoặc Mang đi.
3. Nếu là đơn dùng tại quán, người dùng chọn bàn trống.
4. Nếu là đơn mang đi, hệ thống cho phép tạo đơn mà không cần chọn bàn.
5. Người dùng chọn món và nhập số lượng.
6. Hệ thống tính tổng tiền tạm tính.
7. Người dùng lưu đơn hàng.
8. Hệ thống lưu thông tin đơn hàng và chi tiết đơn hàng.

**Kịch bản ngoại lệ:**

- Nếu chọn bàn đang phục vụ, hệ thống không cho tạo đơn mới trên bàn đó.
- Nếu chưa chọn món, hệ thống yêu cầu thêm món vào đơn hàng.

---

### 4.6 Chỉnh sửa đơn hàng

**Vai trò được sử dụng:** Admin, Thu ngân

**Mục tiêu:** Cho phép thay đổi thông tin đơn hàng trước khi thanh toán.

**Kịch bản chính:**

1. Người dùng chọn đơn hàng cần chỉnh sửa.
2. Hệ thống hiển thị thông tin đơn hàng và danh sách món.
3. Người dùng thêm món, giảm món, thay đổi số lượng hoặc ghi chú.
4. Hệ thống tính lại tổng tiền tạm tính.
5. Người dùng lưu thay đổi.
6. Hệ thống cập nhật thông tin đơn hàng.

**Kịch bản ngoại lệ:**

- Nếu đơn hàng đã thanh toán, hệ thống không cho chỉnh sửa.
- Nếu số lượng món không hợp lệ, hệ thống hiển thị thông báo lỗi.

---

### 4.7 Chọn bàn

**Vai trò được sử dụng:** Admin, Thu ngân

**Mục tiêu:** Chọn bàn cho đơn hàng dùng tại quán.

**Kịch bản chính:**

1. Người dùng tạo đơn hàng dùng tại quán.
2. Hệ thống hiển thị danh sách bàn và trạng thái từng bàn.
3. Người dùng chọn một bàn đang trống.
4. Hệ thống liên kết bàn với đơn hàng.
5. Hệ thống cập nhật trạng thái bàn sang đang phục vụ.

**Kịch bản ngoại lệ:**

- Nếu bàn đang phục vụ hoặc chờ thanh toán, hệ thống không cho chọn bàn đó.

---

### 4.8 Cập nhật trạng thái bàn

**Vai trò được sử dụng:** Admin, Thu ngân

**Mục tiêu:** Cập nhật trạng thái bàn trong quá trình phục vụ.

**Kịch bản chính:**

1. Người dùng mở danh sách bàn.
2. Hệ thống hiển thị trạng thái của từng bàn.
3. Khi tạo đơn tại bàn, hệ thống chuyển trạng thái bàn sang đang phục vụ.
4. Khi khách yêu cầu thanh toán, trạng thái bàn có thể chuyển sang chờ thanh toán.
5. Sau khi thanh toán xong, hệ thống chuyển trạng thái bàn về trống.

**Kịch bản ngoại lệ:**

- Nếu bàn không tồn tại, hệ thống hiển thị thông báo lỗi.
- Nếu trạng thái cập nhật không hợp lệ, hệ thống không lưu thay đổi.

---

### 4.9 Thanh toán và in hóa đơn

**Vai trò được sử dụng:** Admin, Thu ngân

**Mục tiêu:** Thanh toán đơn hàng và in hóa đơn cho khách.

**Kịch bản chính:**

1. Người dùng chọn đơn hàng cần thanh toán.
2. Hệ thống hiển thị chi tiết đơn hàng và tổng tiền.
3. Người dùng chọn phương thức thanh toán.
4. Nếu có khuyến mãi, hệ thống tính số tiền giảm.
5. Hệ thống tính số tiền phải trả.
6. Người dùng xác nhận thanh toán.
7. Hệ thống tạo hóa đơn.
8. Hệ thống cập nhật trạng thái đơn hàng thành đã thanh toán.
9. Nếu đơn hàng dùng tại quán, hệ thống cập nhật trạng thái bàn về trống.
10. Người dùng in hóa đơn cho khách.

**Kịch bản ngoại lệ:**

- Nếu đơn hàng chưa có món, hệ thống không cho thanh toán.
- Nếu phương thức thanh toán chưa được chọn, hệ thống yêu cầu chọn phương thức thanh toán.

---

### 4.10 Tra cứu menu

**Vai trò được sử dụng:** Admin, Thu ngân

**Mục tiêu:** Xem danh sách món đang bán trong quán.

**Kịch bản chính:**

1. Người dùng mở chức năng tra cứu menu.
2. Hệ thống hiển thị danh sách nhóm món và món.
3. Người dùng tìm kiếm món theo tên hoặc nhóm món.
4. Hệ thống hiển thị kết quả phù hợp.

**Kịch bản ngoại lệ:**

- Nếu không có món phù hợp, hệ thống hiển thị thông báo không tìm thấy kết quả.

---

### 4.11 Cập nhật thông tin món

**Vai trò được sử dụng:** Admin

**Mục tiêu:** Cho phép Admin thêm, sửa hoặc ngừng kinh doanh món.

**Kịch bản chính:**

1. Admin mở chức năng cập nhật thông tin món.
2. Admin chọn thêm mới hoặc chỉnh sửa món.
3. Admin nhập thông tin món như tên món, đơn giá, mô tả, hình ảnh và nhóm món.
4. Hệ thống kiểm tra dữ liệu đầu vào.
5. Nếu dữ liệu hợp lệ, hệ thống lưu thông tin món.
6. Hệ thống hiển thị thông báo cập nhật thành công.

**Kịch bản ngoại lệ:**

- Nếu thiếu tên món hoặc đơn giá, hệ thống yêu cầu nhập đầy đủ.
- Nếu đơn giá không hợp lệ, hệ thống hiển thị thông báo lỗi.

---

### 4.12 Cập nhật kho nguyên liệu

**Vai trò được sử dụng:** Admin

**Mục tiêu:** Quản lý nhập kho, xuất kho, kiểm kê và cảnh báo nguyên liệu sắp hết.

**Kịch bản chính:**

1. Admin mở chức năng cập nhật kho nguyên liệu.
2. Admin chọn nhập kho, xuất kho hoặc kiểm kê tồn kho.
3. Admin nhập thông tin nguyên liệu, số lượng và ghi chú.
4. Hệ thống kiểm tra dữ liệu.
5. Hệ thống cập nhật số lượng tồn kho.
6. Nếu số lượng tồn thấp hơn mức tối thiểu, hệ thống hiển thị cảnh báo nguyên liệu sắp hết.

**Kịch bản ngoại lệ:**

- Nếu số lượng nhập hoặc xuất không hợp lệ, hệ thống hiển thị thông báo lỗi.
- Nếu xuất kho vượt quá số lượng tồn, hệ thống không cho lưu phiếu xuất.

---

### 4.13 Cập nhật thông tin khách hàng

**Vai trò được sử dụng:** Admin, Thu ngân

**Mục tiêu:** Lưu trữ và cập nhật thông tin khách hàng phục vụ chăm sóc khách hàng và tra cứu lịch sử mua hàng.

**Kịch bản chính:**

1. Người dùng mở chức năng cập nhật thông tin khách hàng.
2. Người dùng tìm kiếm khách hàng theo tên hoặc số điện thoại.
3. Nếu khách hàng đã tồn tại, hệ thống hiển thị thông tin khách hàng.
4. Người dùng cập nhật thông tin nếu cần.
5. Nếu khách hàng chưa tồn tại, người dùng thêm khách hàng mới.
6. Hệ thống lưu thông tin khách hàng.
7. Người dùng có thể xem lịch sử mua hàng của khách hàng.

**Kịch bản ngoại lệ:**

- Nếu số điện thoại không hợp lệ, hệ thống hiển thị thông báo lỗi.

---

### 4.14 Cập nhật chương trình khuyến mãi

**Vai trò được sử dụng:** Admin

**Mục tiêu:** Cho phép Admin thêm, chỉnh sửa hoặc ngừng áp dụng chương trình khuyến mãi.

**Kịch bản chính:**

1. Admin mở chức năng cập nhật chương trình khuyến mãi.
2. Admin chọn thêm mới hoặc chỉnh sửa chương trình khuyến mãi.
3. Admin nhập thông tin khuyến mãi như tên chương trình, loại khuyến mãi, giá trị giảm, ngày bắt đầu và ngày kết thúc.
4. Hệ thống kiểm tra dữ liệu đầu vào.
5. Nếu dữ liệu hợp lệ, hệ thống lưu chương trình khuyến mãi.
6. Hệ thống hiển thị thông báo cập nhật thành công.

**Kịch bản ngoại lệ:**

- Nếu ngày kết thúc nhỏ hơn ngày bắt đầu, hệ thống hiển thị thông báo lỗi.
- Nếu giá trị giảm không hợp lệ, hệ thống yêu cầu nhập lại.

---

### 4.15 Áp dụng khuyến mãi vào hóa đơn

**Vai trò được sử dụng:** Admin, Thu ngân

**Mục tiêu:** Áp dụng chương trình khuyến mãi hợp lệ vào hóa đơn thanh toán.

**Kịch bản chính:**

1. Người dùng mở đơn hàng cần thanh toán.
2. Người dùng chọn chương trình khuyến mãi đang còn hiệu lực.
3. Hệ thống kiểm tra điều kiện áp dụng khuyến mãi.
4. Nếu hợp lệ, hệ thống tính số tiền giảm.
5. Hệ thống cập nhật số tiền phải trả trên hóa đơn.

**Kịch bản ngoại lệ:**

- Nếu khuyến mãi hết hạn hoặc không hợp lệ, hệ thống không cho áp dụng.

---

### 4.16 Xem báo cáo thống kê

**Vai trò được sử dụng:** Admin

**Mục tiêu:** Cho phép Admin xem các báo cáo phục vụ quản lý hoạt động kinh doanh.

**Kịch bản chính:**

1. Admin mở chức năng xem báo cáo thống kê.
2. Admin chọn loại báo cáo cần xem: doanh thu, món bán chạy hoặc tồn kho nguyên liệu.
3. Admin chọn khoảng thời gian thống kê nếu cần.
4. Hệ thống tổng hợp dữ liệu.
5. Hệ thống hiển thị kết quả báo cáo.

**Kịch bản ngoại lệ:**

- Nếu không có dữ liệu trong khoảng thời gian đã chọn, hệ thống hiển thị thông báo không có dữ liệu.

## 5. Ghi chú triển khai phân quyền

Hệ thống không sử dụng phân quyền động. Các quyền được kiểm soát cố định theo vai trò người dùng.

Khi người dùng đăng nhập, hệ thống kiểm tra vai trò của tài khoản:

- Nếu vai trò là Admin, hệ thống cho phép truy cập toàn bộ chức năng.
- Nếu vai trò là Thu ngân, hệ thống chỉ hiển thị và cho phép sử dụng các chức năng bán hàng được phân quyền.

Cách phân quyền này giúp hệ thống đơn giản, dễ triển khai và phù hợp với phạm vi dự án nhóm nhỏ.
