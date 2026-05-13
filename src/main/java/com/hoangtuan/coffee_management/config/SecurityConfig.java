package com.hoangtuan.coffee_management.config;

import com.hoangtuan.coffee_management.security.CustomLoginSuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CustomLoginSuccessHandler customLoginSuccessHandler
    ) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/uploads/**",
                                "/webjars/**",
                                "/login",
                                "/error/403",
                                "/error/**"
                        ).permitAll()
                        .requestMatchers("/dashboard", "/dashboard/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/nhan-vien/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/vai-tro/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/tai-khoan/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/nhom-mon/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/menu/**", "/mon/xem", "/mon/xem/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_NHANVIEN")
                        .requestMatchers("/mon/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/nguyen-lieu/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/phieu-nhap-kho/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/phieu-xuat-kho/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/bao-cao/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/ban/them", "/ban/them/**", "/ban/sua/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/ban/cap-nhat-trang-thai/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_NHANVIEN")
                        .requestMatchers(
                                "/ban/**",
                                "/ban-hang/**",
                                "/don-hang/**",
                                "/hoa-don/**",
                                "/khach-hang/**",
                                "/ca-nhan/**"
                        ).hasAnyAuthority("ROLE_ADMIN", "ROLE_NHANVIEN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("tenDangNhap")
                        .passwordParameter("matKhau")
                        .successHandler(customLoginSuccessHandler)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex.accessDeniedHandler(accessDeniedHandler()));

        return http.build();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (HttpServletRequest request, HttpServletResponse response, org.springframework.security.access.AccessDeniedException accessDeniedException) ->
                response.sendRedirect(request.getContextPath() + "/error/403");
    }
}
