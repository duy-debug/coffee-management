package com.hoangtuan.coffee_management.service;

import com.hoangtuan.coffee_management.dto.NguyenLieuDetailDTO;
import com.hoangtuan.coffee_management.dto.NguyenLieuFormDTO;
import com.hoangtuan.coffee_management.dto.NguyenLieuListDTO;
import com.hoangtuan.coffee_management.dto.NguyenLieuSearchDTO;
import com.hoangtuan.coffee_management.entity.NguyenLieu;
import java.util.List;

public interface NguyenLieuService {

    List<NguyenLieu> findAll();

    List<NguyenLieu> search(String keyword);

    List<NguyenLieu> findCanhBaoSapHet();

    List<NguyenLieuListDTO> getDanhSach(NguyenLieuSearchDTO searchDTO);

    NguyenLieu findById(String maNguyenLieu);

    NguyenLieuFormDTO getFormThem();

    NguyenLieuFormDTO getFormSua(String maNguyenLieu);

    void save(NguyenLieuFormDTO dto);

    void update(String maNguyenLieu, NguyenLieuFormDTO dto);

    void tangTonKho(String maNguyenLieu, Integer soLuongNhap);

    void giamTonKho(String maNguyenLieu, Integer soLuongXuat);

    boolean kiemTraDuTonKho(String maNguyenLieu, Integer soLuongXuat);

    NguyenLieuDetailDTO getChiTiet(String maNguyenLieu);

    boolean kiemTraSapHet(NguyenLieu nguyenLieu);

    String generateNextMaNguyenLieu();
}
