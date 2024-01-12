package com.joo.digimon.security.filter;

import com.joo.digimon.security.provider.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);
        if (token != null ) {

            try {
                Authentication auth = getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (JwtException | IllegalArgumentException ignore) {
            }


        }
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Authentication getAuthentication(String token) {
        Claims claims =   Jwts.parser()
                .verifyWith(jwtProvider.getKey())
                .build()
                .parseSignedClaims(token).getPayload();

        String userId = claims.getSubject();
        String role = (String) claims.get("role");
        List<SimpleGrantedAuthority> simpleGrantedAuthorities = generateSimpleGrantedAuthorities(role);

        return new UsernamePasswordAuthenticationToken(userId, null, simpleGrantedAuthorities);
    }

    private List<SimpleGrantedAuthority> generateSimpleGrantedAuthorities(String role) {
        List<SimpleGrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();
        switch (role) {
            case "ADMIN":
                simpleGrantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            case "MANAGER":
                simpleGrantedAuthorities.add(new SimpleGrantedAuthority("ROLE_MANAGER"));
            case "USER":
                simpleGrantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                break;
        }

        return simpleGrantedAuthorities;
    }
}
