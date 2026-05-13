package com.hoangtuan.coffee_management.config;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final Path uploadRoot = Paths.get("uploads");
    private final Path monUploadDir = uploadRoot.resolve("mon");

    @PostConstruct
    public void initUploadDirectories() {
        try {
            java.nio.file.Files.createDirectories(monUploadDir);
        } catch (IOException ex) {
            throw new IllegalStateException("Không thể tạo thư mục upload ảnh: " + monUploadDir.toAbsolutePath(), ex);
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = uploadRoot.toUri().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }
}
