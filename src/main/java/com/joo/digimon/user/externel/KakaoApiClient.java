package com.joo.digimon.user.externel;

import com.joo.digimon.user.dto.KakaoUserInfoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(url = "https://kapi.kakao.com/v2/user/me", name = "kakaoInfoClient")
public interface KakaoApiClient {

    @GetMapping(consumes = "application/json")
    KakaoUserInfoResponseDto getKakaoUserInfo(
            @RequestHeader("Authorization") String accessToken);
}
