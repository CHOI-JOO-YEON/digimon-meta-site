package com.joo.digimon.deck.dto;

import com.joo.digimon.deck.model.DeckEntity;
import lombok.Data;

@Data
public class DeckSummaryResponseDto {
    Integer authorId;
    String authorName;
    Integer deckId;
    String deckName;

    public DeckSummaryResponseDto(DeckEntity deck) {
        this.authorId = deck.getUser().getId();
        this.authorName = deck.getUser().getUsername();
        this.deckId = deck.getId();
        this.deckName = deck.getDeckName();
    }
}
