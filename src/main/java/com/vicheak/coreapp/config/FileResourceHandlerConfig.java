package com.vicheak.coreapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileResourceHandlerConfig implements WebMvcConfigurer {

    @Value("${file.server-path}")
    private String serverPath;

    @Value("${file.client-path}")
    private String clientPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(clientPath)
                .addResourceLocations("file:" + serverPath);
    }

}
