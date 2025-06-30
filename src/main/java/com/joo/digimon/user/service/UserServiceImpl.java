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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

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
    private final UserSettingService userSettingService;
    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public LoginResponseDto getKakaoToken(String code) throws IOException {
        KakaoTokenResponseDto authorizationCode =
                kakaoAuthClient.getToken("authorization_code", KAKAO_CLIENT_ID, KAKAO_REDIRECT_URI, code);
        KakaoUserInfoResponseDto kakaoUserInfo =
                kakaoApiClient.getKakaoUserInfo("Bearer " + authorizationCode.getAccessToken());

        String oauthId = kakaoUserInfo.getId();
        User user;

        Optional<User> existing = userRepository.findByOauthId(oauthId);
        if (existing.isPresent()) {
            user = existing.get();
        } else {
            try {
                User newUser = User.builder()
                        .role(Role.USER)
                        .oauthId(oauthId)
                        .nickName(nicknameService.generateNickname())
                        .authSupplier(AuthSupplier.KAKAO)
                        .build();
                user = userRepository.save(newUser);
            } catch (DataIntegrityViolationException ex) {
                user = userRepository.findByOauthId(oauthId)
                        .orElseThrow(() -> new IllegalStateException("Failed to resolve user after conflict", ex));
            }
        }

        userSettingService.initUserSetting(user);

        return new LoginResponseDto(
                jwtProvider.generateToken(user),
                user.getNickName(),
                user.getRole(),
                user.getId()
        );
    }

    @Override
    public LoginResponseDto usernameLogin(UsernameLoginRequestDto usernameLoginRequestDto) throws IOException {
        User user = userRepository.findByUsername(usernameLoginRequestDto.getUsername()).orElseThrow();
        if(!bCryptPasswordEncoder.matches(usernameLoginRequestDto.getPassword(),user.getPassword()))
        {
            return null;
        }
        userSettingService.initUserSetting(user);
        return new LoginResponseDto(jwtProvider.generateToken(user), user.getNickName(), user.getRole(), user.getId());
    }


}
