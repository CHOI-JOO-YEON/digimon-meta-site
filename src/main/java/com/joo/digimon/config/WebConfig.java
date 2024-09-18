package com.joo.digimon.config;

import com.joo.digimon.util.RequestLoggingInterceptor;
import com.joo.digimon.util.StringToEnumConverterFactory;
import com.joo.digimon.util.UserArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final UserArgumentResolver userArgumentResolver;
    private final RequestLoggingInterceptor requestLoggingInterceptor;


    /*
    cors 설정은 SecurityConfig에서 관리 중
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:*", "https://digimon-meta.site")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new StringToEnumConverterFactory());
    }
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(userArgumentResolver);
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(requestLoggingInterceptor)
//                .addPathPatterns("/**");
    }
}