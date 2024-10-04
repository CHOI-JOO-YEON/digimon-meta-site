package com.joo.digimon.card.service;

import com.joo.digimon.card.dto.CardDto;
import com.joo.digimon.card.dto.CreateNoteDto;
import com.joo.digimon.card.dto.ResponseNoteDto;
import com.joo.digimon.card.dto.UpdateNoteDto;
import com.joo.digimon.card.model.CardImgEntity;
import com.joo.digimon.card.model.NoteEntity;
import com.joo.digimon.card.repository.CardImgRepository;
import com.joo.digimon.card.repository.NoteRepository;
import com.joo.digimon.global.exception.model.CanNotDeleteException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardAdminServiceImpl implements CardAdminService {
    private final CardImgRepository cardImgRepository;
    private final NoteRepository noteRepository;

    @Value("${domain.url}")
    private String prefixUrl;

    @Override
    public List<CardDto> getAllCard() {
        List<CardImgEntity> cardImgRepositoryAll = cardImgRepository.findAll();

        List<CardDto> allCards = new ArrayList<>();

        for (CardImgEntity cardImgEntity : cardImgRepositoryAll) {
            allCards.add(new CardDto(cardImgEntity, prefixUrl));
        }

        return allCards;
    }

    @Override
    @Transactional
    public List<ResponseNoteDto> createNote(CreateNoteDto createNoteDto) {
        noteRepository.save(NoteEntity.builder()
                .name(createNoteDto.getName())
                .cardOrigin(createNoteDto.getCardOrigin())
                .releaseDate(createNoteDto.getReleaseDate())
                .build());

        return getAllResponseNoteDto();
    }

    @Override
    @Transactional
    public List<ResponseNoteDto> deleteNote(Integer noteId) {
        Optional<NoteEntity> noteEntityOptional = noteRepository.findById(noteId);
        if(noteEntityOptional.isEmpty()) {
            throw new NoSuchElementException();
        }
        if(noteEntityOptional.get().getCardImgEntities().size()>1){
            throw new CanNotDeleteException("연관관계인 카드가 있어 삭제에 실패했습니다.");
        }

        noteRepository.deleteById(noteId);

        return getAllResponseNoteDto();
    }

    @Override
    @Transactional
    public List<ResponseNoteDto> updateNotes(List<UpdateNoteDto> updateNoteDtoList) {
        for (UpdateNoteDto updateNoteDto : updateNoteDtoList) {
            Optional<NoteEntity> note = noteRepository.findById(updateNoteDto.getNoteId());
            if(note.isEmpty()){
                throw new NoSuchElementException();
            }
            note.get().update(updateNoteDto);
        }

        return getAllResponseNoteDto();
    }

    private List<ResponseNoteDto> getAllResponseNoteDto() {
        List<ResponseNoteDto> responseNoteDtos = new ArrayList<>();
        List<NoteEntity> noteEntities = noteRepository.findAll();
        for (NoteEntity noteEntity : noteEntities) {
            responseNoteDtos.add(new ResponseNoteDto(noteEntity));
        }
        return responseNoteDtos;
    }
}
