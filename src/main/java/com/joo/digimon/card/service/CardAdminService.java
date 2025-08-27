package com.joo.digimon.card.service;

import com.joo.digimon.card.dto.card.CardAdminPutDto;
import com.joo.digimon.card.dto.card.TraitDto;
import com.joo.digimon.card.dto.card.TypeMergeRequestDto;
import com.joo.digimon.card.dto.note.CreateNoteDto;
import com.joo.digimon.card.dto.note.ResponseNoteDto;
import com.joo.digimon.card.dto.note.UpdateNoteDto;
import com.joo.digimon.card.dto.type.TypeDto;
import org.springframework.http.HttpStatusCode;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CardAdminService {
    List<ResponseNoteDto> createNote(CreateNoteDto createNoteDto);
    List<ResponseNoteDto> deleteNote(Integer noteId);
    List<ResponseNoteDto> putNotes(List<UpdateNoteDto> updateNoteDtoList);
    @Transactional
    void updateCards(List<CardAdminPutDto> cardAdminPutDtoList);

    List<TypeDto> getAllType();

    List<TypeDto> getAllTypeDetail();

    List<TypeDto> deleteType(Integer typeId);

    List<TypeDto> putTypes(List<TypeDto> typeDtoList);

    List<ResponseNoteDto> getNotes();

    void mergeTypeToKorean(TypeMergeRequestDto dto);

    void deleteDuplicateCardCombineType();
    
    Boolean createCardJsonUpdateToGitHubPR(String message);
  
    TraitDto getAllTraits();
  
    TypeDto getCardByTypeId(Integer typeId);
  
    void originUrlSet();

    Boolean createCard(CardAdminPutDto cardAdminPutDto, MultipartFile image) throws IOException;

    Boolean createType(String name);
}
