package com.joo.digimon.crawling.procedure.img;

import com.joo.digimon.card.model.CardEntity;
import com.joo.digimon.card.model.CardImgEntity;
import com.joo.digimon.card.model.NoteEntity;
import com.joo.digimon.card.repository.CardImgRepository;
import com.joo.digimon.crawling.dto.ReflectCardRequestDto;
import com.joo.digimon.global.enums.Locale;

public class OtherLocaleCardImgProcessor implements CardImgProcessor {

    private final CardImgRepository cardImgRepository;

    public OtherLocaleCardImgProcessor(CardImgRepository cardImgRepository) {
        this.cardImgRepository = cardImgRepository;
    }

    @Override
    public void process(ReflectCardRequestDto dto, CardEntity cardEntity, NoteEntity noteEntity) {
        if (cardEntity.getCardImgEntity() == null || cardEntity.getCardImgEntity().isEmpty()) {
//            boolean isEnCard = dto.getLocale() == Locale.ENG;
//            boolean isJpnCard = dto.getLocale() == Locale.JPN;

            CardImgEntity cardImgEntity = CardImgEntity.builder()
                    .isParallel(false)
                    .noteEntity(noteEntity)
                    .crawlingCardEntity(dto.getCrawlingCardEntity())
                    .cardEntity(cardEntity)
//                    .isEnCard(isEnCard)
//                    .isJpnCard(isJpnCard)
                    .build();

            cardImgRepository.save(cardImgEntity);
        }
    }
}
