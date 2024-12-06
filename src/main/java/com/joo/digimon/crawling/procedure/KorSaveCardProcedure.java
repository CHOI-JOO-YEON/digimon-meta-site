package com.joo.digimon.crawling.procedure;

import com.joo.digimon.card.model.CardCombineTypeEntity;
import com.joo.digimon.card.model.CardEntity;
import com.joo.digimon.card.model.NoteEntity;
import com.joo.digimon.card.model.TypeEntity;
import com.joo.digimon.card.repository.CardCombineTypeRepository;
import com.joo.digimon.card.repository.CardRepository;
import com.joo.digimon.card.repository.NoteRepository;
import com.joo.digimon.card.repository.TypeRepository;
import com.joo.digimon.crawling.dto.ReflectCardRequestDto;
import com.joo.digimon.crawling.model.CrawlingCardEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class KorSaveCardProcedure implements SaveCardProcedure {
    CardRepository cardRepository;
    CardCombineTypeRepository cardCombineTypeRepository;
    TypeRepository typeRepository;
    NoteRepository noteRepository;
    ReflectCardRequestDto dto;

    public KorSaveCardProcedure(CardRepository cardRepository, CardCombineTypeRepository cardCombineTypeRepository, TypeRepository typeRepository, NoteRepository noteRepository, ReflectCardRequestDto dto) {
        this.cardRepository = cardRepository;
        this.cardCombineTypeRepository = cardCombineTypeRepository;
        this.typeRepository = typeRepository;
        this.noteRepository = noteRepository;
        this.dto = dto;
    }

    @Override
    public CardEntity getOrCreateCardEntity() {
        return cardRepository.findByCardNo(dto.getCardNo())
                .orElseGet(() -> {
                    CardEntity newEntity = CardEntity.builder()
                            .cardName(dto.getCardName())
                            .effect(dto.getEffect())
                            .sourceEffect(dto.getSourceEffect())
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
                            .cardName(dto.getCardName())
                            .effect(dto.getEffect())
                            .sourceEffect(dto.getSourceEffect())
                            .build();
                    return cardRepository.save(newEntity);
                });
    }

    @Override
    public Set<CardCombineTypeEntity> getCardCombineTypes(CardEntity cardEntity) {
        return dto.getTypes().stream()
                .map(type -> {
                    TypeEntity typeEntity = typeRepository.findByName(type)
                            .orElseGet(() -> typeRepository.save(TypeEntity.builder().name(type).build()));

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
