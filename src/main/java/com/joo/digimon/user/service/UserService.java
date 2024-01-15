package com.joo.digimon.user.service;

import com.joo.digimon.user.dto.LoginResponseDto;
import com.joo.digimon.user.dto.UsernameLoginRequestDto;

import java.io.IOException;

public interface UserService {

    LoginResponseDto getKakaoToken(String code) throws IOException;

    LoginResponseDto usernameLogin(UsernameLoginRequestDto usernameLoginRequestDto) throws IOException;
}
