package com.joo.digimon.card.service;

import com.joo.digimon.card.dto.*;

import java.util.List;

public interface CardService {
    CardResponseDto searchCards(CardRequestDto cardRequestDto);

    AdminCardResponseDto searchAdminCards(CardRequestDto cardRequestDto);

    List<ResponseNoteDto> getNotes();

    List<CardTypeResponseDto> getTypes();
}
