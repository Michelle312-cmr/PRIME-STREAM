package com.example.authapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = Path.of("uploads").toAbsolutePath().normalize().toUri().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);

        String projectVideos = Path.of("src", "main", "resources", "static", "videos").toAbsolutePath().normalize().toUri().toString();
        String externalVideos = Path.of("videos").toAbsolutePath().normalize().toUri().toString();
        registry.addResourceHandler("/videos/**")
                .addResourceLocations(projectVideos, externalVideos, "classpath:/static/videos/");
    }
}
