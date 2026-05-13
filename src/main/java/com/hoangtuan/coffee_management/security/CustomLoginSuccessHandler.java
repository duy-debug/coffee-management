package com.hoangtuan.coffee_management.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean isAdmin = authorities.stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));

        boolean isNhanVien = authorities.stream()
                .anyMatch(authority -> "ROLE_NHANVIEN".equals(authority.getAuthority()));

        if (isAdmin) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        if (isNhanVien) {
            response.sendRedirect(request.getContextPath() + "/ban-hang");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/login?error");
    }
}
