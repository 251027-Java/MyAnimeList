package com.animeapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    @Autowired
    private BasicAuthInterceptor basicAuthInterceptor;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // all endpoints
                        .allowedOrigins("http://localhost:4200") // allow Angular
                        .allowedMethods("GET", "POST", "PUT", "DELETE");
            }

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(basicAuthInterceptor)
                        .addPathPatterns("/**")
                        .excludePathPatterns("/user/login", "/user/register", "/anime/all");
            }
        };
    }
}