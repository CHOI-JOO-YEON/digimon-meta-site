package com.joo.digimon.user.externel;


import com.joo.digimon.user.dto.KakaoTokenResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "KakaoClient", url = "https://kauth.kakao.com/oauth/token")
public interface KakaoAuthClient {

    @PostMapping(consumes = "application/json")
    KakaoTokenResponseDto getToken(@RequestParam("grant_type") String grantType,
                                   @RequestParam("client_id") String clientId,
                                   @RequestParam("redirect_uri") String redirectUri,
                                   @RequestParam("code") String code);
}
