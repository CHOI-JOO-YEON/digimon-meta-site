package com.joo.digimon.security.provider;

import com.joo.digimon.exception.model.UnAuthorizationException;
import com.joo.digimon.user.model.User;
import com.joo.digimon.user.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    @Value("${jwt.secret}")
    private String JWT_SECRET;

    @Value("${jwt.expiration-ms}")
    private long JWT_EXPIRATION_MS;

    @Getter
    private SecretKey key;

    private final UserRepository userRepository;

    @PostConstruct
    void generateKey() {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET));
    }

    public String generateToken(User user) throws IOException {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date exp = new Date(nowMillis + JWT_EXPIRATION_MS);

        return Jwts.builder()
                .subject(user.getUserIdentify())
                .claim("auth-supplier", user.getAuthSupplier().name())
                .claim("role", user.getRole().name())
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
    }

    public User getUserFromToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);

            Claims claims = claimsJws.getPayload();
            String auth = claims.get("auth-supplier", String.class);
            if (auth.equals("USERNAME")) {
                return userRepository.findByUsername(claims.getSubject()).orElseThrow();
            }
            return userRepository.findByOauthId(claims.getSubject()).orElseThrow();
        } catch (ExpiredJwtException e) {
            throw new UnAuthorizationException("Token has expired");
        } catch (JwtException e) {
            throw new UnAuthorizationException("Invalid token");
        }


    }

    public String getJwtFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JWT_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
