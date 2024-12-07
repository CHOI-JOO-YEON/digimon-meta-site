package com.joo.digimon.crawling.procedure.save;

import com.joo.digimon.card.model.CardCombineTypeEntity;
import com.joo.digimon.card.model.CardEntity;
import com.joo.digimon.card.model.EnglishCardEntity;
import com.joo.digimon.card.model.TypeEntity;
import com.joo.digimon.card.repository.*;
import com.joo.digimon.crawling.dto.ReflectCardRequestDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

public class EngSaveCardProcedure implements SaveCardProcedure {
    CardRepository cardRepository;
    EnglishCardRepository englishCardRepository;
    CardCombineTypeRepository cardCombineTypeRepository;
    TypeRepository typeRepository;
    NoteRepository noteRepository;
    ReflectCardRequestDto dto;

    public EngSaveCardProcedure(CardRepository cardRepository, EnglishCardRepository englishCardRepository, CardCombineTypeRepository cardCombineTypeRepository, TypeRepository typeRepository, NoteRepository noteRepository, ReflectCardRequestDto dto) {
        this.cardRepository = cardRepository;
        this.englishCardRepository = englishCardRepository;
        this.cardCombineTypeRepository = cardCombineTypeRepository;
        this.typeRepository = typeRepository;
        this.noteRepository = noteRepository;
        this.dto = dto;
    }

    @Override
    @Transactional
    public CardEntity getOrCreateCardEntity() {
        CardEntity cardEntity = cardRepository.findByCardNo(dto.getCardNo())
                .orElseGet(() -> {
                    CardEntity newEntity = CardEntity.builder()
                            .cardNo(dto.getCardNo())
                            .rarity(dto.getRarity())
                            .cardType(dto.getCardType())
                            .lv(dto.getLv())
                            .form(dto.getForm())
                            .attribute(dto.getAttribute())
                            .dp(dto.getDp())
                            .playCost(dto.getPlayCost())
                            .digivolveCost1(dto.getDigivolveCost1())
                            .digivolveCost2(dto.getDigivolveCost2())
                            .digivolveCondition1(dto.getDigivolveCondition1())
                            .digivolveCondition2(dto.getDigivolveCondition2())
                            .color1(dto.getColor1())
                            .color2(dto.getColor2())
                            .color3(dto.getColor3())
                            .sortString(SaveCardProcedure.generateSortString(dto.getCardNo()))
                            .isOnlyEnCard(true)
                            .build();
                    return cardRepository.save(newEntity);
                });

        if (cardEntity.getEnglishCard() == null) {
            EnglishCardEntity englishCardEntity = englishCardRepository.save(EnglishCardEntity.builder()
                    .effect(dto.getEffect())
                    .sourceEffect(dto.getSourceEffect())
                    .cardName(dto.getCardName())
                    .originUrl(dto.getOriginUrl())
                    .cardEntity(cardEntity)
                    .build());
            cardEntity.updateEnglishCard(englishCardEntity);
        }

        return cardEntity;
    }

    @Override
    @Transactional
    public Set<CardCombineTypeEntity> getCardCombineTypes(CardEntity cardEntity) {
        return dto.getTypes().stream()
                .map(type -> {
                    TypeEntity typeEntity = typeRepository.findByEngName(type)
                            .orElseGet(() -> typeRepository.save(TypeEntity.builder().engName(type).build()));

                    return cardCombineTypeRepository.save(
                            CardCombineTypeEntity.builder()
                                    .cardEntity(cardEntity)
                                    .typeEntity(typeEntity)
                                    .build()
                    );
                })
                .collect(Collectors.toSet());
    }
}
