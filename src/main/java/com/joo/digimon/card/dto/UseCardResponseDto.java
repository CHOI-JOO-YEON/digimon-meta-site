package com.joo.digimon.card.dto;

import lombok.Data;

@Data
public class UseCardResponseDto {
    Integer rank;
    Double ratio;
    Long count;
    CardVo card;
}
