package com.joo.digimon.card.service;

import com.joo.digimon.card.dto.CardDto;
import com.joo.digimon.card.dto.CreateNoteDto;
import com.joo.digimon.card.dto.ResponseNoteDto;
import com.joo.digimon.card.dto.UpdateNoteDto;

import java.util.List;

public interface CardAdminService {
    List<CardDto> getAllCard();
    List<ResponseNoteDto> createNote(CreateNoteDto createNoteDto);
    List<ResponseNoteDto> deleteNote(Integer noteId);
    List<ResponseNoteDto> updateNotes(List<UpdateNoteDto> updateNoteDtoList);
}
