package com.joo.digimon.card.service;

import com.joo.digimon.card.dto.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CardAdminService {
    List<CardAdminResponseDto> getAllCard();
    List<ResponseNoteDto> createNote(CreateNoteDto createNoteDto);
    List<ResponseNoteDto> deleteNote(Integer noteId);
    List<ResponseNoteDto> putNotes(List<UpdateNoteDto> updateNoteDtoList);

    @Transactional
    List<CardAdminResponseDto> updateCards(List<CardAdminRequestDto> cardAdminRequestDtoList);
}
