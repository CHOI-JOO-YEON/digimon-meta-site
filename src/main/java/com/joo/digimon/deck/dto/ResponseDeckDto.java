package com.joo.digimon.deck.dto;

import com.joo.digimon.card.dto.CardDto;
import com.joo.digimon.card.model.CardImgEntity;
import com.joo.digimon.global.enums.Color;
import com.joo.digimon.deck.model.DeckCardEntity;
import com.joo.digimon.deck.model.DeckColor;
import com.joo.digimon.deck.model.DeckEntity;
import lombok.Data;
import lombok.Getter;

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


    @Getter
    private class Card extends CardDto {
        Integer cnt;
        public Card(CardImgEntity card,Integer cnt, String prefixUrl) {
            super(card,prefixUrl);
            this.cnt=cnt;
        }



        public Card(DeckCardEntity deckCardEntity, String prefixUrl) {
            super(deckCardEntity.getCardImgEntity(),prefixUrl);
            this.cnt=deckCardEntity.getCnt();
        }

    }
}
