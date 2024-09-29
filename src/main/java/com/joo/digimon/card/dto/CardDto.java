package com.joo.digimon.card.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.joo.digimon.card.model.CardImgEntity;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardDto extends CardVo {

    Boolean isEn;
    LocalDateTime modifiedAt;

    public CardDto(CardImgEntity card, String prefixUrl) {
        super(card, prefixUrl);
        this.isEn = card.getIsEnCard();
        this.modifiedAt = card.getModifiedAt() == null ?
                LocalDateTime.of(1998, 7, 15, 9, 30) : card.getModifiedAt();
    }


}
