package com.joo.digimon.card.dto.card;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.joo.digimon.card.model.CardCombineTypeEntity;
import com.joo.digimon.card.model.CardImgEntity;
import com.joo.digimon.card.model.TypeEntity;
import com.joo.digimon.global.enums.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardVo {
    Integer cardId;
    String cardNo;
    Integer lv;
    Integer dp;
    Integer playCost;
    Integer digivolveCost1;
    Integer digivolveCondition1;
    Integer digivolveCost2;
    Integer digivolveCondition2;
    Color color1;
    Color color2;
    Color color3;
    Rarity rarity;
    CardType cardType;
    Form form;
    Attribute attribute;
    List<String> types;
    Boolean isParallel;
    String sortString;
    String noteName;
    Integer noteId;
    LocalDate releaseDate;
    Boolean isEn;
    LocalDateTime modifiedAt;
    List<LocaleCardData> localeCardData;

    public CardVo(CardImgEntity card, String prefixUrl) {
        this.cardId = card.getId();
        this.cardNo = card.getCardEntity().getCardNo();
        this.lv = card.getCardEntity().getLv();
        this.dp = card.getCardEntity().getDp();
        this.playCost = card.getCardEntity().getPlayCost();
        this.digivolveCost1 = card.getCardEntity().getDigivolveCost1();
        this.digivolveCondition1 = card.getCardEntity().getDigivolveCondition1();
        this.digivolveCost2 = card.getCardEntity().getDigivolveCost2();
        this.digivolveCondition2 = card.getCardEntity().getDigivolveCondition2();
        this.color1 = card.getCardEntity().getColor1();
        this.color2 = card.getCardEntity().getColor2();
        this.color3 = card.getCardEntity().getColor3();
        this.rarity = card.getCardEntity().getRarity();
        this.cardType = card.getCardEntity().getCardType();
        this.form = card.getCardEntity().getForm();
        this.attribute = card.getCardEntity().getAttribute();

        types = card.getCardEntity().getCardCombineTypeEntities()
                .stream()
                .map(CardCombineTypeEntity::getTypeEntity)
                .map(TypeEntity::getName)
                .filter(Objects::nonNull)
                .toList();

        this.isParallel = card.getIsParallel();
        this.sortString = card.getCardEntity().getSortString();
        this.releaseDate = card.getCardEntity().getReleaseDate();
        this.noteName = card.getNoteEntity().getName();
        this.noteId = card.getNoteEntity().getId();
        this.isEn = card.getIsEnCard();
        this.modifiedAt = card.getModifiedAt() == null ?
                LocalDateTime.of(1998, 7, 15, 9, 30) : card.getModifiedAt();

        localeCardData = new ArrayList<>();

        if (card.getCardEntity().getCardName() != null) {
            String imgUrl = null;
            String smallImgUrl = null;
            if(!Boolean.TRUE.equals(card.getCardEntity().getIsOnlyEnCard())) 
            {
                imgUrl = prefixUrl + card.getBigWebpUrl();
                smallImgUrl = prefixUrl + card.getSmallWebpUrl();
            }
            
            localeCardData.add(new LocaleCardData(
                    card.getCardEntity().getCardName(),
                    card.getCardEntity().getEffect(),
                    card.getCardEntity().getSourceEffect(),
                    Locale.KOR,
                    imgUrl,
                    smallImgUrl
            ));
        }

        if (card.getCardEntity().getEnglishCard() != null) {
            localeCardData.add(new LocaleCardData(
                    card.getCardEntity().getEnglishCard().getCardName(),
                    card.getCardEntity().getEnglishCard().getEffect(),
                    card.getCardEntity().getEnglishCard().getSourceEffect(),
                    Locale.ENG,
                    prefixUrl + card.getCardEntity().getEnglishCard().getWebpUrl(),
                    prefixUrl + card.getCardEntity().getEnglishCard().getWebpUrl()
            ));
        }

        if (card.getCardEntity().getJapaneseCardEntity() != null) {
            localeCardData.add(new LocaleCardData(
                    card.getCardEntity().getJapaneseCardEntity().getCardName(),
                    card.getCardEntity().getJapaneseCardEntity().getEffect(),
                    card.getCardEntity().getJapaneseCardEntity().getSourceEffect(),
                    Locale.JPN,
                    prefixUrl + card.getCardEntity().getJapaneseCardEntity().getWebpUrl(),
                    prefixUrl + card.getCardEntity().getJapaneseCardEntity().getWebpUrl()
            ));
        }

    }


}
