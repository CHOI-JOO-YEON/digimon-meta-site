package com.joo.digimon.card.service;

import com.joo.digimon.card.dto.*;
import com.joo.digimon.card.model.*;
import com.joo.digimon.card.repository.*;
import com.joo.digimon.global.exception.model.CanNotDeleteException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardAdminServiceImpl implements CardAdminService {
    private final CardImgRepository cardImgRepository;
    private final NoteRepository noteRepository;
    private final EnglishCardRepository englishCardRepository;
    private final CardCombineTypeRepository cardCombineTypeRepository;
    private final TypeRepository typeRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Value("${domain.url}")
    private String prefixUrl;

    @Override
    @Transactional
    public List<CardAdminResponseDto> getAllCard() {
        List<CardImgEntity> cardImgRepositoryAll = cardImgRepository.findAll();

        List<CardAdminResponseDto> allCards = new ArrayList<>();

        for (CardImgEntity cardImgEntity : cardImgRepositoryAll) {
            allCards.add(new CardAdminResponseDto(cardImgEntity, prefixUrl));
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
        if (noteEntityOptional.isEmpty()) {
            throw new NoSuchElementException();
        }
        if (noteEntityOptional.get().getCardImgEntities().size() > 1) {
            throw new CanNotDeleteException("연관관계인 카드가 있어 삭제에 실패했습니다.");
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
    public List<CardAdminResponseDto> updateCards(List<CardAdminRequestDto> cardAdminRequestDtoList) {
        for (CardAdminRequestDto cardAdminRequestDto : cardAdminRequestDtoList) {
            CardImgEntity cardImgEntity = getCardImgEntity(cardAdminRequestDto);

            updateCardEnglishProperty(cardAdminRequestDto, cardImgEntity);
            cardImgEntity.update(cardAdminRequestDto);
            updateType(cardAdminRequestDto, cardImgEntity);
        }
//        entityManager.flush();
//        entityManager.clear();
        return getAllCard();
    }

    @Transactional
    public CardImgEntity getCardImgEntity(CardAdminRequestDto cardAdminRequestDto) {
        Optional<CardImgEntity> optionalCardImg = cardImgRepository.findById(cardAdminRequestDto.getCardId());
        if (optionalCardImg.isEmpty()) {
            throw new NoSuchElementException();
        }
        return optionalCardImg.get();
    }

    @Transactional
    public void updateType(CardAdminRequestDto cardAdminRequestDto, CardImgEntity cardImgEntity) {
        if (cardAdminRequestDto.getTypes()!=null) {
            Set<String> types = cardAdminRequestDto.getTypes();
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
    public void updateCardEnglishProperty(CardAdminRequestDto cardAdminRequestDto, CardImgEntity cardImgEntity) {
        if (!isEngPresent(cardAdminRequestDto)) {
            return;
        }
        EnglishCardEntity englishCard = cardImgEntity.getCardEntity().getEnglishCard();
        if (englishCard == null) {
            englishCard = EnglishCardEntity.builder().card(cardImgEntity.getCardEntity()).build();
        }
        englishCard.update(cardAdminRequestDto);
        englishCardRepository.save(englishCard);
    }

    private boolean isEngPresent(CardAdminRequestDto cardAdminRequestDto) {
        return cardAdminRequestDto.getCardEngName()!=null || cardAdminRequestDto.getEngEffect()!=null || cardAdminRequestDto.getEngSourceEffect()!=null;
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
