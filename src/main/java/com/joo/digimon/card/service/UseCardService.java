package com.joo.digimon.card.service;

import com.joo.digimon.card.dto.UseCardResponseDto;

import java.util.List;

public interface UseCardService {
    List<UseCardResponseDto> findTopUsedCardsWithACard(Integer cardImgId, Integer formatId);
}
