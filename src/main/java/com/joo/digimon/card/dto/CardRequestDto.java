package com.joo.digimon.card.dto;

import com.joo.digimon.annotation.valid.OrderValid;
import com.joo.digimon.crawling.enums.CardType;
import com.joo.digimon.crawling.enums.Color;
import com.joo.digimon.crawling.enums.Rarity;
import lombok.Data;

import java.util.Set;

@Data
public class CardRequestDto {
    String searchString;
    Integer noteId;
    Set<Color> colors;
    Integer colorOperation=1; //0 = and, 1 = or
    Set<Integer> lvs;

    Set<CardType> cardTypes;

    Integer minPlayCost =0; //
    Integer maxPlayCost =20;
    Integer minDp=1000;
    Integer maxDp=16000;
    Integer minDigivolutionCost=0;
    Integer MaxDigivolutionCost=8;
    Set<Rarity> rarities;

    Integer page = 1;
    Integer size = 20;

    Integer parallelOption = 0; // 0= all, 1= onlyNormal, 2=onlyParallel

    @OrderValid
    String orderOption = "cardNo";
    Boolean isOrderDesc = false;


}
