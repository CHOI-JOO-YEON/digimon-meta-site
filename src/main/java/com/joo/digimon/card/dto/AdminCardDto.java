package com.joo.digimon.card.dto;

import com.joo.digimon.card.model.CardCombineTypeEntity;
import com.joo.digimon.card.model.CardImgEntity;
import com.joo.digimon.global.enums.CardType;
import com.joo.digimon.global.enums.Color;
import com.joo.digimon.global.enums.Form;
import com.joo.digimon.global.enums.Rarity;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class AdminCardDto {
    Integer cardId;
    String cardNo;
    String cardName;
    String cardEngName;
    Integer lv;
    Integer dp;
    Integer playCost;
    Integer digivolveCost1;
    Integer digivolveCondition1;
    Integer digivolveCost2;
    Integer digivolveCondition2;
    String effect;
    String engEffect;
    String sourceEffect;
    String engSourceEffect;
    Color color1;
    Color color2;
    Color color3;
    Rarity rarity;
    CardType cardType;
    Form form;
    String attributes;
    List<String> types;
    String imgUrl;
    String smallImgUrl;
    Boolean isParallel;
    String sortString;
    String noteName;
    Integer noteId;
    LocalDate releaseDate;
    Boolean isEn;
    LocalDateTime modifiedAt;

    public AdminCardDto(CardImgEntity card, String prefixUrl) {

        Optional.ofNullable(card.getCardEntity().getEnglishCard()).ifPresent(
                englishCardEntity -> {
                    Optional.ofNullable(englishCardEntity.getCardName()).ifPresent(value -> this.cardEngName = value);
                    Optional.ofNullable(englishCardEntity.getEffect()).ifPresent(value -> this.engEffect = value);
                    Optional.ofNullable(englishCardEntity.getSourceEffect()).ifPresent(value -> this.engSourceEffect = value);
                }
        );
        this.cardName = card.getCardEntity().getCardName();
        this.effect = card.getCardEntity().getEffect();
        this.sourceEffect = card.getCardEntity().getSourceEffect();
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
        this.attributes = card.getCardEntity().getAttribute();
        this.types = new ArrayList<>();
        for (CardCombineTypeEntity cardCombineTypeEntity : card.getCardEntity().getCardCombineTypeEntities()) {
            types.add(cardCombineTypeEntity.getTypeEntity().getName());
        }
        this.imgUrl = prefixUrl + card.getUploadUrl();
        this.isParallel = card.getIsParallel();
        this.sortString = card.getCardEntity().getSortString();
        this.smallImgUrl = prefixUrl + card.getSmallImgUrl();
        this.releaseDate = card.getCardEntity().getReleaseDate();
        this.noteName = card.getNoteEntity().getName();
        this.noteId = card.getNoteEntity().getId();
        this.isEn = card.getIsEnCard();
        this.modifiedAt = card.getModifiedAt() == null ?
                LocalDateTime.of(1998, 7, 15, 9, 30) : card.getModifiedAt();
    }
}
