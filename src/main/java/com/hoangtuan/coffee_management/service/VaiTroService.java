package com.hoangtuan.coffee_management.service;

import com.hoangtuan.coffee_management.entity.VaiTro;
import java.util.List;

public interface VaiTroService {

    List<VaiTro> getVaiTroCoDinh();

    VaiTro getByTenVaiTro(String tenVaiTro);
}
