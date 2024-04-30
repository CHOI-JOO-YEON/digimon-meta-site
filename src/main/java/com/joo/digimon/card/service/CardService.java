package com.joo.digimon.card.service;

import com.joo.digimon.card.dto.CardRequestDto;
import com.joo.digimon.card.dto.CardResponseDto;
import com.joo.digimon.card.dto.CardTypeResponseDto;
import com.joo.digimon.card.dto.NoteDto;

import java.util.List;

public interface CardService {
    CardResponseDto searchCards(CardRequestDto cardRequestDto);

    List<NoteDto> getNotes();

    List<CardTypeResponseDto> getTypes();
}
