package com.hoangtuan.coffee_management.service;

import com.hoangtuan.coffee_management.dto.NhomMonFormDTO;
import com.hoangtuan.coffee_management.entity.NhomMon;
import java.util.List;

public interface NhomMonService {

    List<NhomMon> findAll();

    NhomMon findById(String maNhomMon);

    NhomMonFormDTO getFormThem();

    NhomMonFormDTO getFormSua(String maNhomMon);

    void save(NhomMonFormDTO dto);

    void update(String maNhomMon, NhomMonFormDTO dto);

    String generateNextMaNhomMon();
}
