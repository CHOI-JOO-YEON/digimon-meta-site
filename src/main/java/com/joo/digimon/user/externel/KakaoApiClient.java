package com.joo.digimon.user.externel;

import com.joo.digimon.user.dto.KakaoUserInfoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(url = "${spring.security.oauth2.client.provider.kakao.user-info-uri}", name = "kakaoInfoClient")
public interface KakaoApiClient {

    @GetMapping(consumes = "application/json")
    KakaoUserInfoResponseDto getKakaoUserInfo(
            @RequestHeader("Authorization") String accessToken);
}
