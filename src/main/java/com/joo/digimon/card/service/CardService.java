package com.joo.digimon.card.service;

import com.joo.digimon.card.dto.CardRequestDto;
import com.joo.digimon.card.dto.CardResponseDto;

public interface CardService {
    CardResponseDto searchCards(CardRequestDto cardRequestDto);
}
