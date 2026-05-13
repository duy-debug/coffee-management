package com.hoangtuan.coffee_management.service;

import com.hoangtuan.coffee_management.dto.MonFormDTO;
import com.hoangtuan.coffee_management.dto.MonSearchDTO;
import com.hoangtuan.coffee_management.entity.Mon;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface MonService {

    List<Mon> findAll();

    List<Mon> findDangBan();

    List<Mon> searchAndFilter(MonSearchDTO searchDTO, boolean chiXemDangBan);

    Mon findById(String maMon);

    MonFormDTO getFormThem();

    MonFormDTO getFormSua(String maMon);

    default void save(MonFormDTO dto) {
        save(dto, null);
    }

    void save(MonFormDTO dto, MultipartFile hinhAnhFile);

    default void update(String maMon, MonFormDTO dto) {
        update(maMon, dto, null);
    }

    void update(String maMon, MonFormDTO dto, MultipartFile hinhAnhFile);

    void ngungBan(String maMon);

    void moBan(String maMon);

    String generateNextMaMon();
}
