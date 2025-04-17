package com.joo.digimon.crawling.procedure.img;

import com.joo.digimon.card.model.CardEntity;
import com.joo.digimon.card.model.CardImgEntity;
import com.joo.digimon.card.model.NoteEntity;
import com.joo.digimon.card.repository.CardImgRepository;
import com.joo.digimon.crawling.dto.ReflectCardRequestDto;

import java.util.List;

public class KorCardImgProcessor implements CardImgProcessor {
    private final CardImgRepository cardImgRepository;

    public KorCardImgProcessor(CardImgRepository cardImgRepository) {
        this.cardImgRepository = cardImgRepository;
    }

    @Override
    public void process(ReflectCardRequestDto dto, CardEntity cardEntity, NoteEntity noteEntity) {
        if (!dto.getIsParallel()) {
            List<CardImgEntity> cardImgEntityList = cardImgRepository.findByCardEntityAndIsParallelFalse(cardEntity);
            if (!cardImgEntityList.isEmpty()) {
                CardImgEntity existingCardImg = cardImgEntityList.getFirst();
                updateExistingCardImg(existingCardImg, dto, noteEntity);
            } else {
                createNewCardImg(dto, cardEntity, noteEntity, false);
            }
        } else {
            createNewCardImg(dto, cardEntity, noteEntity, true);
        }
    }

    private void updateExistingCardImg(CardImgEntity existingCardImg, ReflectCardRequestDto dto, NoteEntity noteEntity) {
        existingCardImg.updateNote(noteEntity);
        existingCardImg.updateCrawlingCardEntity(dto.getCrawlingCardEntity());
        existingCardImg.updateOriginUrl(dto.getOriginUrl());
//        existingCardImg.updateIsEnCard(false);
//        existingCardImg.updateIsJpnCard(false);

        cardImgRepository.save(existingCardImg);
    }

    private void createNewCardImg(ReflectCardRequestDto dto, CardEntity cardEntity, NoteEntity noteEntity, boolean isParallel) {
        CardImgEntity cardImgEntity = CardImgEntity.builder()
                .isParallel(isParallel)
                .noteEntity(noteEntity)
                .crawlingCardEntity(dto.getCrawlingCardEntity())
                .cardEntity(cardEntity)
                .originUrl(dto.getOriginUrl())
//                .isEnCard(false)
//                .isJpnCard(false)
                .build();

        cardImgRepository.save(cardImgEntity);
    }
}
