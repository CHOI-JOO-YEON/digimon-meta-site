package com.joo.digimon.user.service;

import com.joo.digimon.security.provider.JwtProvider;
import com.joo.digimon.user.dto.*;
import com.joo.digimon.user.enums.AuthSupplier;
import com.joo.digimon.user.enums.Role;
import com.joo.digimon.user.externel.KakaoApiClient;
import com.joo.digimon.user.externel.KakaoAuthClient;
import com.joo.digimon.user.model.User;
import com.joo.digimon.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String KAKAO_CLIENT_ID;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String KAKAO_REDIRECT_URI;

    private final UserRepository userRepository;
    private final KakaoAuthClient kakaoAuthClient;
    private final KakaoApiClient kakaoApiClient;
    private final NicknameService nicknameService;
    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public LoginResponseDto getKakaoToken(String code) throws IOException {
        KakaoTokenResponseDto authorizationCode = kakaoAuthClient.getToken("authorization_code", KAKAO_CLIENT_ID, KAKAO_REDIRECT_URI, code);
        KakaoUserInfoResponseDto kakaoUserInfo = kakaoApiClient.getKakaoUserInfo("Bearer " + authorizationCode.getAccessToken());
        User user = userRepository.findByOauthId(kakaoUserInfo.getId()).orElseGet(
                () -> userRepository.save(
                        User.builder()
                                .role(Role.USER)
                                .oauthId(kakaoUserInfo.getId())
                                .nickName(nicknameService.generateNickname())
                                .authSupplier(AuthSupplier.KAKAO)
                                .build()));



        return new LoginResponseDto(jwtProvider.generateToken(user), user.getNickName(), user.getRole());
    }

    @Override
    public LoginResponseDto usernameLogin(UsernameLoginRequestDto usernameLoginRequestDto) throws IOException {
        User user = userRepository.findByUsername(usernameLoginRequestDto.getUsername()).orElseThrow();
        if(!bCryptPasswordEncoder.matches(usernameLoginRequestDto.getPassword(),user.getPassword()))
        {
            return null;
        }
        return new LoginResponseDto(jwtProvider.generateToken(user), user.getNickName(), user.getRole());
    }


}
