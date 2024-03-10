package com.joo.digimon.deck.dto;

import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
public class RequestDeckDto {
    Integer deckId;
    String deckName="My Deck";
    Map<Integer,Integer> cardAndCntMap;
    Set<String> colors;
    Integer formatId;
    Boolean isPublic = false;
}
