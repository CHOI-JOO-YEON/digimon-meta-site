package com.joo.digimon.card.service;

import com.joo.digimon.card.dto.use_card.UseCardResponseDto;

public interface UseCardService {
    UseCardResponseDto findTopUsedCardsWithACard(Integer cardImgId, Integer formatId);
}
