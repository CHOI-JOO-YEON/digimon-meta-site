package com.joo.digimon.card.dto.use_card;

import com.joo.digimon.card.dto.card.CardVo;
import lombok.Data;

@Data
public class UseCard {
    Integer rank;
    Double ratio;
    Long count;
    CardVo card;
}
