package com.joo.digimon.card.dto;

import lombok.Data;

@Data
public class UseCard {
    Integer rank;
    Double ratio;
    Long count;
    CardVo card;
}
