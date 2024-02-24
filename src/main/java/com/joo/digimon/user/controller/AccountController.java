package com.joo.digimon.user.controller;

import com.joo.digimon.user.dto.LoginResponseDto;
import com.joo.digimon.user.dto.UsernameLoginRequestDto;
import com.joo.digimon.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {
    private final UserService userService;

    @GetMapping("/token/kakao")
    public ResponseEntity<?> getKakaoToken(@RequestParam String code, HttpServletResponse response) throws IOException {
        LoginResponseDto kakaoToken = userService.getKakaoToken(code);
        Cookie jwtCookie = new Cookie("JWT_TOKEN", kakaoToken.getAccessToken());
        jwtCookie.setHttpOnly(true);
        jwtCookie.setMaxAge(60 * 60 * 24); // 예: 24시간
        jwtCookie.setPath("/");
        response.addCookie(jwtCookie);
        return new ResponseEntity<>(kakaoToken,HttpStatus.OK);
    }

    @PostMapping("/login/username")
    public ResponseEntity<?> loginUsername(@RequestBody UsernameLoginRequestDto usernameLoginRequestDto) throws IOException {
        return new ResponseEntity<>(userService.usernameLogin(usernameLoginRequestDto),HttpStatus.OK);
    }


}
