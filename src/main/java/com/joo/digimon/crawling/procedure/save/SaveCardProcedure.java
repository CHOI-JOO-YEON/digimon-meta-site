package com.joo.digimon.crawling.procedure.save;

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
    static String generateSortString(String cardNo) {
        StringBuilder stringBuilder = new StringBuilder();
        if (cardNo.startsWith("BT")) {
            stringBuilder.append("A");
        } else if (cardNo.startsWith("ST")) {
            stringBuilder.append("B");
        } else if (cardNo.startsWith("EX")) {
            stringBuilder.append("C");
        } else if (cardNo.startsWith("RB")) {
            stringBuilder.append("D");
        } else if (cardNo.startsWith("LM")) {
            stringBuilder.append("E");
            String[] parts = cardNo.split("-");
            String firstNumberPart = String.format("%03d", Integer.parseInt(parts[1].replaceAll("\\D", "")));
            stringBuilder.append(firstNumberPart);
            return stringBuilder.toString();
        } else if (cardNo.startsWith("P")) {
            stringBuilder.append("Z");
            String[] parts = cardNo.split("-");
            String firstNumberPart = String.format("%03d", Integer.parseInt(parts[1].replaceAll("\\D", "")));
            stringBuilder.append(firstNumberPart);
            return stringBuilder.toString();
        }

        String[] parts = cardNo.split("-");

        String firstNumberPart = String.format("%03d", Integer.parseInt(parts[0].replaceAll("\\D", "")));
        stringBuilder.append(firstNumberPart);

        if (parts.length > 1) {
            String secondNumberPart = String.format("%03d", Integer.parseInt(parts[1]));
            stringBuilder.append(secondNumberPart);
        }

        return stringBuilder.toString();
    }


    CardEntity getOrCreateCardEntity();
    Set<CardCombineTypeEntity> getCardCombineTypes(CardEntity cardEntity);

}
