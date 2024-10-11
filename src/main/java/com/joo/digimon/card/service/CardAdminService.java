package com.joo.digimon.card.service;

import com.joo.digimon.card.dto.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CardAdminService {
    List<AdminCardDto> getAllCard();
    List<ResponseNoteDto> createNote(CreateNoteDto createNoteDto);
    List<ResponseNoteDto> deleteNote(Integer noteId);
    List<ResponseNoteDto> putNotes(List<UpdateNoteDto> updateNoteDtoList);
    @Transactional
    List<AdminCardDto> updateCards(List<CardAdminRequestDto> cardAdminRequestDtoList);

    List<TypeDto> getAllType();
}
