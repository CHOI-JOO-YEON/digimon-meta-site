package com.joo.digimon.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KakaoUserInfoResponseDto {
    private String id;

    @JsonProperty("connected_at")
    private LocalDateTime connectedAt;
}
