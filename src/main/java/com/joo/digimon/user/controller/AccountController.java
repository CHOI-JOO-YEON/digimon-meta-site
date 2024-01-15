package com.joo.digimon.user.controller;

import com.joo.digimon.user.dto.UsernameLoginRequestDto;
import com.joo.digimon.user.service.UserService;
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
    public ResponseEntity<?> getKakaoToken(@RequestParam String code) throws IOException {
        return new ResponseEntity<>(userService.getKakaoToken(code),HttpStatus.OK);
    }

    @PostMapping("/login/username")
    public ResponseEntity<?> loginUsername(@RequestBody UsernameLoginRequestDto usernameLoginRequestDto) throws IOException {
        return new ResponseEntity<>(userService.usernameLogin(usernameLoginRequestDto),HttpStatus.OK);
    }


}
