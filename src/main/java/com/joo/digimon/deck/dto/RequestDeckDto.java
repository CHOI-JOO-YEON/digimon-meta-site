package com.joo.digimon.deck.dto;

import lombok.Data;

import java.util.Map;

@Data
public class RequestDeckDto {
    Integer deckId;
    String deckName="My Deck";
    Map<Integer,Integer> cardAndCntMap;
    Boolean isPublic = true;
}
