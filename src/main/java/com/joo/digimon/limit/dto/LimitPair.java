package com.joo.digimon.limit.dto;

import com.joo.digimon.card.model.CardEntity;
import com.joo.digimon.limit.model.LimitPairCardEntity;
import com.joo.digimon.limit.model.LimitPairEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class LimitPair {
    List<String> aCardPairNos;
    List<String> bCardPairNos;

    public LimitPair(LimitPairEntity limitPair) {
        this.aCardPairNos = limitPair.getPairACardSet().stream().map(LimitPairCardEntity::getCardEntity).map(CardEntity::getCardNo).toList();
        this.bCardPairNos = limitPair.getPairBCardSet().stream().map(LimitPairCardEntity::getCardEntity).map(CardEntity::getCardNo).toList();
    }
}
