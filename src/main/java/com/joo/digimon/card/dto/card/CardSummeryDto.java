package com.joo.digimon.card.dto.card;

import com.joo.digimon.card.model.CardEntity;
import lombok.Data;

@Data
public class CardSummeryDto {
    Integer id;
    String name;
    String cardNo;

    public CardSummeryDto(CardEntity cardEntity) {
        this.id = cardEntity.getId();
        this.name=cardEntity.getCardName();
        this.cardNo=cardEntity.getCardNo();
    }
}
