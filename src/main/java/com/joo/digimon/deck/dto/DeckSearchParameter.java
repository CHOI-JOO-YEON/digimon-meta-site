package com.joo.digimon.deck.dto;

import com.joo.digimon.crawling.enums.Color;
import lombok.Data;

import java.util.Set;

@Data
public class DeckSearchParameter {
    Boolean isMyDeck;
    Set<Color> colors;
    Integer colorOperation; //0 = or, 1 = and
    Integer formatId;
    String searchString;
    Integer page;
    Integer size;


}
