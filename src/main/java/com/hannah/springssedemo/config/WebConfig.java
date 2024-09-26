package com.hannah.springssedemo.config;

import com.hannah.springssedemo.interceptor.JwtTokenInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final JwtTokenInterceptor jwtTokenInterceptor;


//    @Value("${cors.allowed-headers}")
//    private String allowedHeaders;
//
//    @Value("${cors.allowed-methods}")
//    private String allowedMethods;
//
//    @Value("${cors.allowed-origin-patterns}")
//    private String allowedOriginPatterns;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(jwtTokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/login"
                );
    }

}
