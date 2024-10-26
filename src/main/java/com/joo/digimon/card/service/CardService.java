package com.joo.digimon.card.service;

import com.joo.digimon.card.dto.card.CardResponseDto;
import com.joo.digimon.card.dto.card.CardSearchRequestDto;
import com.joo.digimon.card.dto.note.ResponseNoteDto;
import com.joo.digimon.card.dto.type.CardTypeResponseDto;

import java.util.List;

public interface CardService {
    CardResponseDto searchCards(CardSearchRequestDto cardSearchRequestDto);

    CardResponseDto searchAdminCards(CardSearchRequestDto cardSearchRequestDto);

    List<ResponseNoteDto> getNotes();

    List<CardTypeResponseDto> getTypes();
}
