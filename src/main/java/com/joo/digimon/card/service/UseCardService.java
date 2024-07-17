package com.joo.digimon.card.service;

import com.joo.digimon.card.dto.UseCard;
import com.joo.digimon.card.dto.UseCardResponseDto;

import java.util.List;

public interface UseCardService {
    UseCardResponseDto findTopUsedCardsWithACard(Integer cardImgId, Integer formatId);
}
