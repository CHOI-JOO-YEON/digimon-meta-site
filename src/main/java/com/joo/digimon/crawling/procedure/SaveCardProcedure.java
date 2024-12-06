package com.joo.digimon.crawling.procedure;

import com.joo.digimon.card.model.CardCombineTypeEntity;
import com.joo.digimon.card.model.CardEntity;
import jakarta.transaction.Transactional;

import java.util.Set;

public interface SaveCardProcedure {

    @Transactional
    default CardEntity execute() {
        CardEntity cardEntity = getOrCreateCardEntity();
        if(cardEntity.getCardCombineTypeEntities() == null || cardEntity.getCardCombineTypeEntities().isEmpty()) {
            cardEntity.updateCardCombineTypes(getCardCombineTypes(cardEntity));
        }


        return cardEntity;
    }
    CardEntity getOrCreateCardEntity();
    Set<CardCombineTypeEntity> getCardCombineTypes(CardEntity cardEntity);

}
