package com.joo.digimon.deck.dto;

import com.joo.digimon.global.enums.Color;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class RequestDeckDto {
    Integer deckId;
    String deckName="My Deck";
    Map<Integer, Integer> cardAndCntMap;
    List<Color> colors=new ArrayList<>();
    Integer formatId=0;
    Boolean isPublic = false;
    String description="";
}
