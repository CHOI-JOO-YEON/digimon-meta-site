package com.joo.digimon.card.service;

import com.joo.digimon.card.dto.CardRequestDto;
import com.joo.digimon.card.dto.CardResponseDto;
import com.joo.digimon.card.dto.CardTypeResponseDto;
import com.joo.digimon.card.dto.ResponseNoteDto;

import java.util.List;

public interface CardService {
    CardResponseDto searchCards(CardRequestDto cardRequestDto);

    List<ResponseNoteDto> getNotes();

    List<CardTypeResponseDto> getTypes();
}
