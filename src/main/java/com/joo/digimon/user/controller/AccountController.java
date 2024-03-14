package com.joo.digimon.user.controller;

import com.joo.digimon.user.dto.LoginResponseDto;
import com.joo.digimon.user.dto.UsernameLoginRequestDto;
import com.joo.digimon.user.service.NicknameService;
import com.joo.digimon.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {
    private final UserService userService;
    private final NicknameService nicknameService;

    @GetMapping("/token/kakao")
    public ResponseEntity<?> getKakaoToken(@RequestParam String code, HttpServletResponse response) throws IOException {
        LoginResponseDto loginResponseDto = userService.getKakaoToken(code);
        setTokenCookie(response, loginResponseDto);
        return new ResponseEntity<>(loginResponseDto, HttpStatus.OK);
    }

    @PostMapping("/login/username")
    public ResponseEntity<?> loginUsername(@RequestBody UsernameLoginRequestDto usernameLoginRequestDto, HttpServletResponse response) throws IOException {
        LoginResponseDto loginResponseDto = userService.usernameLogin(usernameLoginRequestDto);
        setTokenCookie(response, loginResponseDto);
        return new ResponseEntity<>(loginResponseDto, HttpStatus.OK);
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout( HttpServletResponse response) {
        invalidateTokenCookie(response);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private static void setTokenCookie(HttpServletResponse response, LoginResponseDto loginResponseDto) {
        Cookie jwtCookie = new Cookie("JWT_TOKEN", loginResponseDto.getAccessToken());
        jwtCookie.setHttpOnly(true);
        jwtCookie.setMaxAge(60 * 60 * 24);
        jwtCookie.setPath("/");
        response.addCookie(jwtCookie);
    }

    private static void invalidateTokenCookie(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("JWT_TOKEN", "");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setMaxAge(0);
        jwtCookie.setPath("/");
        response.addCookie(jwtCookie);
    }

}
