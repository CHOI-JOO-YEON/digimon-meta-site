package com.joo.digimon.user.service;


import com.joo.digimon.card.model.CardEntity;
import com.joo.digimon.global.enums.CardType;

import java.util.List;

public interface NicknameService {
    String generateNickname();

    List<CardEntity> findByCardTypeAndIsOnlyEnCardIsNullOrIsFalse(CardType cardType);
}
