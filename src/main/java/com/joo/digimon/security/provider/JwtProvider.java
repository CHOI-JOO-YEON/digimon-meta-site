package com.joo.digimon.security.provider;

import com.joo.digimon.user.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Date;

@Component
public class JwtProvider {
    @Value("${jwt.secret}")
    private String JWT_SECRET;

    @Value("${jwt.expiration-ms}")
    private long JWT_EXPIRATION_MS;

    @Getter
    private SecretKey key;

    @PostConstruct
    void generateKey(){
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

}
