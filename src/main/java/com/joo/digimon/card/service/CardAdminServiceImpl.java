package com.joo.digimon.card.service;

import com.joo.digimon.card.dto.card.CardAdminPutDto;
import com.joo.digimon.card.dto.note.CreateNoteDto;
import com.joo.digimon.card.dto.note.ResponseNoteDto;
import com.joo.digimon.card.dto.note.UpdateNoteDto;
import com.joo.digimon.card.dto.type.TypeDto;
import com.joo.digimon.card.model.*;
import com.joo.digimon.card.repository.*;
import com.joo.digimon.global.exception.model.CanNotDeleteException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CardAdminServiceImpl implements CardAdminService {
    private final CardImgRepository cardImgRepository;
    private final NoteRepository noteRepository;
    private final EnglishCardRepository englishCardRepository;
    private final CardCombineTypeRepository cardCombineTypeRepository;
    private final TypeRepository typeRepository;

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
        if (noteEntityOptional.isEmpty()) {
            throw new NoSuchElementException();
        }
        if (noteEntityOptional.get().getCardImgEntities().size() > 1) {
            throw new CanNotDeleteException("연관 관계인 카드가 있어 삭제에 실패했습니다.");
        }

        noteRepository.deleteById(noteId);

        return getAllResponseNoteDto();
    }

    @Override
    @Transactional
    public List<ResponseNoteDto> putNotes(List<UpdateNoteDto> updateNoteDtoList) {
        for (UpdateNoteDto updateNoteDto : updateNoteDtoList) {
            Optional<NoteEntity> note = noteRepository.findById(updateNoteDto.getNoteId());
            if (note.isEmpty()) {
                throw new NoSuchElementException();
            }
            note.get().putNote(updateNoteDto);
        }

        return getAllResponseNoteDto();
    }

    @Transactional
    @Override
    public void updateCards(List<CardAdminPutDto> cardAdminPutDtoList) {
        for (CardAdminPutDto cardAdminPutDto : cardAdminPutDtoList) {
            CardImgEntity cardImgEntity = getCardImgEntity(cardAdminPutDto);

            updateCardEnglishProperty(cardAdminPutDto, cardImgEntity);
            cardImgEntity.update(cardAdminPutDto);
            NoteEntity noteEntity = noteRepository.findById(cardAdminPutDto.getNoteId()).orElseThrow();
            cardImgEntity.updateNote(noteEntity);
            updateType(cardAdminPutDto, cardImgEntity);
        }
    }

    @Override
    public List<TypeDto> getAllType() {
        List<TypeEntity> types = typeRepository.findAll();
        List<TypeDto> typeDtoList = new ArrayList<>();
        for (TypeEntity type : types) {
            typeDtoList.add(new TypeDto(type));
        }
        return typeDtoList;
    }

    @Override
    @Transactional
    public List<TypeDto> deleteType(Integer typeId) {
        Optional<TypeEntity> typeEntityOptional = typeRepository.findById(typeId);
        if (typeEntityOptional.isEmpty()) {
            throw new NoSuchElementException();
        }
        if (typeEntityOptional.get().getCardCombineTypes().size() > 1) {
            throw new CanNotDeleteException("연관 관계인 카드가 있어 삭제에 실패했습니다.");
        }
        typeRepository.deleteById(typeId);
        return getAllType();
    }

    @Override
    @Transactional
    public List<TypeDto> putTypes(List<TypeDto> typeDtoList) {
        for (TypeDto typeDto : typeDtoList) {
            Optional<TypeEntity> typeEntityOptional = typeRepository.findById(typeDto.getTypeId());
            if (typeEntityOptional.isEmpty()) {
                throw new NoSuchElementException();
            }
            typeEntityOptional.get().putType(typeDto);
        }
        return getAllType();
    }


    @Transactional
    public CardImgEntity getCardImgEntity(CardAdminPutDto cardAdminPutDto) {
        Optional<CardImgEntity> optionalCardImg = cardImgRepository.findById(cardAdminPutDto.getCardId());
        if (optionalCardImg.isEmpty()) {
            throw new NoSuchElementException();
        }
        return optionalCardImg.get();
    }

    @Transactional
    public void updateType(CardAdminPutDto cardAdminPutDto, CardImgEntity cardImgEntity) {
        if (cardAdminPutDto.getTypes()!=null) {
            Set<String> types = cardAdminPutDto.getTypes();
            cardCombineTypeRepository.deleteAll(cardImgEntity.getCardEntity().getCardCombineTypeEntities());
            Set<CardCombineTypeEntity> cardCombineTypeEntities = new HashSet<>();
            for (String type : types) {
                TypeEntity typeEntity;
                if(typeRepository.findByName(type).isPresent()) {
                    typeEntity = typeRepository.findByName(type).get();
                }else{
                    typeEntity = typeRepository.save(TypeEntity.builder().name(type).build());
                }
                cardCombineTypeEntities.add(
                        CardCombineTypeEntity.builder()
                                .cardEntity(cardImgEntity.getCardEntity())
                                .typeEntity(typeEntity)
                                .build()
                );
            }
            cardCombineTypeRepository.saveAll(cardCombineTypeEntities);
            cardImgEntity.updateType(cardCombineTypeEntities);
        }

    }

    @Transactional
    public void updateCardEnglishProperty(CardAdminPutDto cardAdminPutDto, CardImgEntity cardImgEntity) {
        if (!isEngPresent(cardAdminPutDto)) {
            return;
        }
        EnglishCardEntity englishCard = cardImgEntity.getCardEntity().getEnglishCard();
        if (englishCard == null) {
            englishCard = EnglishCardEntity.builder().cardEntity(cardImgEntity.getCardEntity()).build();
        }
        englishCard.update(cardAdminPutDto);
        englishCardRepository.save(englishCard);
    }

    private boolean isEngPresent(CardAdminPutDto cardAdminPutDto) {
        return cardAdminPutDto.getCardEngName()!=null || cardAdminPutDto.getEngEffect()!=null || cardAdminPutDto.getEngSourceEffect()!=null;
    }

    private List<ResponseNoteDto> getAllResponseNoteDto() {
        List<ResponseNoteDto> responseNoteDtos = new ArrayList<>();
        List<NoteEntity> noteEntities = noteRepository.findAll();
        for (NoteEntity noteEntity : noteEntities) {
            responseNoteDtos.add(new ResponseNoteDto(noteEntity));
        }
        return responseNoteDtos;
    }

    @Override
    public List<ResponseNoteDto> getNotes() {
        List<ResponseNoteDto> noteDtoList = new ArrayList<>();
        List<NoteEntity> noteEntityList = noteRepository.findAll();
        for (NoteEntity noteEntity : noteEntityList) {
            noteDtoList.add(new ResponseNoteDto(noteEntity));
        }

        return noteDtoList;
    }
}
