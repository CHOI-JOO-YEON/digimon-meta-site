package com.joo.digimon.card.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.joo.digimon.card.model.CardCombineTypeEntity;
import com.joo.digimon.card.model.CardImgEntity;
import com.joo.digimon.global.enums.CardType;
import com.joo.digimon.global.enums.Color;
import com.joo.digimon.global.enums.Form;
import com.joo.digimon.global.enums.Rarity;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardResponseDto {


    Integer totalPages;
    Integer currentPage;
    Integer totalElements;
    List<Card> cards;


    public CardResponseDto(Page<CardImgEntity> page, String prefixUrl) {
        this.totalPages = page.getTotalPages();
        this.totalElements = Math.toIntExact(page.getTotalElements());
        this.currentPage = page.getNumber();
        cards = new ArrayList<>();
        for (CardImgEntity cardImgEntity : page) {
            cards.add(new Card(cardImgEntity, prefixUrl));
        }
    }

    public CardResponseDto(List<CardImgEntity> entities, String prefixUrl, int currentPage, int totalElements, int totalPages) {
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.currentPage = currentPage;
        cards = new ArrayList<>();
        for (CardImgEntity cardImgEntity : entities) {
            cards.add(new Card(cardImgEntity, prefixUrl));
        }
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class Card {
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

        public Card(CardImgEntity card, String prefixUrl) {
            this.cardId = card.getId();
            this.cardNo = card.getCardEntity().getCardNo();
            this.cardName = card.getCardEntity().getCardName();
            this.lv = card.getCardEntity().getLv();
            this.dp = card.getCardEntity().getDp();
            this.playCost = card.getCardEntity().getPlayCost();
            this.digivolveCost1 = card.getCardEntity().getDigivolveCost1();
            this.digivolveCondition1 = card.getCardEntity().getDigivolveCondition1();
            this.digivolveCost2 = card.getCardEntity().getDigivolveCost2();
            this.digivolveCondition2 = card.getCardEntity().getDigivolveCondition2();
            this.effect = card.getCardEntity().getEffect();
            this.sourceEffect = card.getCardEntity().getSourceEffect();
            this.color1 = card.getCardEntity().getColor1();
            this.color2 = card.getCardEntity().getColor2();
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
            this.sortString=card.getCardEntity().getSortString();
            this.smallImgUrl=prefixUrl+card.getSmallImgUrl();
            this.releaseDate=card.getCardEntity().getReleaseDate();
            this.noteName = card.getNoteEntity().getName();
            this.noteId=card.getId();
        }

    }
}
