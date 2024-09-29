package com.joo.digimon.card.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.joo.digimon.card.model.CardImgEntity;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardDto extends CardVo {

    Boolean isEn;

    public CardDto(CardImgEntity card, String prefixUrl) {
        super(card, prefixUrl);
        this.isEn = card.getIsEnCard();
    }


}
