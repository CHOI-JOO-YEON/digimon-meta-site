package com.joo.digimon.card.dto.card;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.joo.digimon.card.model.CardCombineTypeEntity;
import com.joo.digimon.card.model.CardImgEntity;
import com.joo.digimon.card.model.TypeEntity;
import com.joo.digimon.global.enums.CardType;
import com.joo.digimon.global.enums.Color;
import com.joo.digimon.global.enums.Form;
import com.joo.digimon.global.enums.Rarity;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardVo {
    Integer cardId;
    String cardNo;
    String cardName;
    Integer lv;
    Integer dp;
    Integer playCost;
    Integer digivolveCost1;
    Integer digivolveCondition1;
    Integer digivolveCost2;
    Integer digivolveCondition2;
    String effect;
    String sourceEffect;
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

    String engName;
    String engEffect;
    String engSourceEffect;

    public CardVo(CardImgEntity card, String prefixUrl) {
        this.cardName = card.getCardEntity().getCardName();
        this.effect = card.getCardEntity().getEffect();
        this.sourceEffect = card.getCardEntity().getSourceEffect();
        this.engName = card.getCardEntity().getEnglishCard().getCardName();
        this.engEffect = card.getCardEntity().getEnglishCard().getEffect();
        this.engSourceEffect = card.getCardEntity().getEnglishCard().getSourceEffect();
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

        types = card.getCardEntity().getCardCombineTypeEntities()
                .stream()
                .map(CardCombineTypeEntity::getTypeEntity)
                .map(TypeEntity::getName)
                .filter(Objects::nonNull)
                .toList();

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
