package com.joo.digimon.card.dto;

import com.joo.digimon.global.annotation.valid.OrderValid;
import com.joo.digimon.global.enums.CardType;
import com.joo.digimon.global.enums.Color;
import com.joo.digimon.global.enums.Rarity;
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
    Integer maxDp=17000;
    Integer minDigivolutionCost=0;
    Integer MaxDigivolutionCost=8;
    Set<Rarity> rarities;

    Integer page = 1;
    Integer size = 20;

    Integer parallelOption = 0; // 0= all, 1= onlyNormal, 2=onlyParallel

    Boolean isEnglishCardInclude = false;

    @OrderValid
    String orderOption = "cardNo";
    Boolean isOrderDesc = false;


    Set<Integer> typeIds;
    Integer typeOperation=1; //0 = and, 1 = or


}
