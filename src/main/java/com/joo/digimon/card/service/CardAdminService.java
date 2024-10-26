package com.joo.digimon.card.service;

import com.joo.digimon.card.dto.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CardAdminService {
    List<ResponseNoteDto> createNote(CreateNoteDto createNoteDto);
    List<ResponseNoteDto> deleteNote(Integer noteId);
    List<ResponseNoteDto> putNotes(List<UpdateNoteDto> updateNoteDtoList);
    @Transactional
    void updateCards(List<CardAdminPutDto> cardAdminPutDtoList);

    List<TypeDto> getAllType();

    List<TypeDto> deleteType(Integer typeId);

    List<TypeDto> putTypes(List<TypeDto> typeDtoList);

    List<ResponseNoteDto> getNotes();
}
