package com.joo.digimon.config;

import com.joo.digimon.security.entry_point.CustomAuthenticationEntryPoint;
import com.joo.digimon.security.filter.JwtFilter;
import com.joo.digimon.security.handler.CustomAccessDeniedHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;


    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${app.logout-redirect-uri}")
    private String logoutRedirectUri;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .headers(headers -> headers.frameOptions(
                        HeadersConfigurer.FrameOptionsConfig::disable))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPoint))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .oauth2Login(oauth2 -> {})
                .authorizeHttpRequests(
                        request -> request
                                .requestMatchers("/api/crawling/**").hasRole("ADMIN")
                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/format/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/format/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/limit/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/limit/**").permitAll()
                                .requestMatchers("/api/manager/**").hasRole("MANAGER")
                                .requestMatchers("/h2-console/**").permitAll()
                                .requestMatchers("/api/account/**").permitAll()
                                .requestMatchers("/api/deck/import/**").permitAll()
                                .requestMatchers(HttpMethod.GET,"/api/deck/**").permitAll()
                                .requestMatchers("/api/card/**").permitAll()

                                .anyRequest().authenticated())
                .sessionManagement(session -> session.
                        sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .deleteCookies("JWT_TOKEN")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            String kakaoLogoutUrl = UriComponentsBuilder
                                    .fromHttpUrl("https://kauth.kakao.com/oauth/logout")
                                    .queryParam("client_id", kakaoClientId)
                                    .queryParam("logout_redirect_uri", logoutRedirectUri)
                                    .build()
                                    .toUriString();

                            response.sendRedirect(kakaoLogoutUrl);
                        })
                        .permitAll()
                );


        return http.build();

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("http://localhost:9999");
        configuration.addAllowedOriginPattern("http://localhost:50000");
        configuration.addAllowedOriginPattern("https://digimon-meta.site");
        configuration.addAllowedOriginPattern("https://dgchub.com");
        configuration.addAllowedOriginPattern("https://admin.dgchub.com");
//        configuration.addAllowedOriginPattern("https://digimon-meta.site/*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
