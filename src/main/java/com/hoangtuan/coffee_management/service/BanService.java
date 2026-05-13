package com.hoangtuan.coffee_management.service;

import com.hoangtuan.coffee_management.dto.BanFormDTO;
import com.hoangtuan.coffee_management.dto.BanSearchDTO;
import com.hoangtuan.coffee_management.entity.Ban;
import java.util.List;

public interface BanService {

    List<Ban> findAll();

    Ban findById(String maBan);

    List<Ban> searchAndFilter(BanSearchDTO searchDTO);

    List<Ban> findByTrangThai(String trangThai);

    List<Ban> findBanTrong();

    BanFormDTO getFormThem();

    BanFormDTO getFormSua(String maBan);

    void save(BanFormDTO dto);

    void update(String maBan, BanFormDTO dto);

    void updateTrangThai(String maBan, String trangThai);

    String generateNextMaBan();
}
