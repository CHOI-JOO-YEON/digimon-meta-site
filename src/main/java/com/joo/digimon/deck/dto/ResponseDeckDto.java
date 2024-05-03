package com.joo.digimon.deck.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.joo.digimon.card.model.CardCombineTypeEntity;
import com.joo.digimon.card.model.CardImgEntity;
import com.joo.digimon.global.enums.CardType;
import com.joo.digimon.global.enums.Color;
import com.joo.digimon.global.enums.Form;
import com.joo.digimon.global.enums.Rarity;
import com.joo.digimon.deck.model.DeckCardEntity;
import com.joo.digimon.deck.model.DeckColor;
import com.joo.digimon.deck.model.DeckEntity;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class ResponseDeckDto {
    Integer authorId;
    String authorName;
    Integer deckId;
    String deckName;
    List<Card> cards;
    Set<Color> colors;
    Integer formatId;

    public ResponseDeckDto(DeckEntity deck, String prefixUrl) {
        this.authorId = deck.getUser().getId();
        this.authorName = deck.getUser().getNickName();
        this.deckId = deck.getId();
        this.deckName=deck.getDeckName();
        cards = new ArrayList<>();
        for (DeckCardEntity deckCardEntity : deck.getDeckCardEntities()) {
            cards.add(new Card(deckCardEntity,prefixUrl));
        }
        colors = new HashSet<>();
        for (DeckColor deckColor : deck.getDeckColors()) {
            colors.add(deckColor.getColor());
        }
        formatId=deck.getFormat().getId();
    }


    public ResponseDeckDto() {
        this.cards = new ArrayList<>();
    }

    public void addCard(CardImgEntity cardImgEntity, Integer cnt, String prefixUrl) {
        cards.add(new Card(cardImgEntity,cnt,prefixUrl));
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class Card {
        Integer cnt;
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
        Boolean isEn;
        public Card(CardImgEntity card,Integer cnt, String prefixUrl) {
            this.cnt=cnt;
            createCard(card, prefixUrl);
        }

        private void createCard(CardImgEntity card, String prefixUrl) {
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
            this.sortString= card.getCardEntity().getSortString();
            this.smallImgUrl= prefixUrl + card.getSmallImgUrl();
            this.releaseDate=card.getCardEntity().getReleaseDate();
            this.isEn=Boolean.TRUE.equals( card.getIsEnCard());
            this.noteId = card.getNoteEntity().getId();
            this.noteName=card.getNoteEntity().getName();
        }

        public Card(DeckCardEntity deckCardEntity, String prefixUrl) {
            cnt=deckCardEntity.getCnt();
            CardImgEntity card = deckCardEntity.getCardImgEntity();
            createCard(card, prefixUrl);
        }

    }
}
