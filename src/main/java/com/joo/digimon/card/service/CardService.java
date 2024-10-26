package com.joo.digimon.card.service;

import com.joo.digimon.card.dto.*;

import java.util.List;

public interface CardService {
    CardResponseDto searchCards(CardSearchRequestDto cardSearchRequestDto);

    CardResponseDto searchAdminCards(CardSearchRequestDto cardSearchRequestDto);

    List<ResponseNoteDto> getNotes();

    List<CardTypeResponseDto> getTypes();
}
