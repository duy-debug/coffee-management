package com.hoangtuan.coffee_management.service;

import com.hoangtuan.coffee_management.dto.CapNhatCaNhanDTO;
import com.hoangtuan.coffee_management.dto.DoiMatKhauDTO;
import com.hoangtuan.coffee_management.dto.ThongTinCaNhanDTO;

public interface CaNhanService {

    ThongTinCaNhanDTO getCurrentUserProfile(String tenDangNhap);

    void updateCurrentUserProfile(String tenDangNhap, CapNhatCaNhanDTO dto);

    void changePassword(String tenDangNhap, DoiMatKhauDTO dto);
}
