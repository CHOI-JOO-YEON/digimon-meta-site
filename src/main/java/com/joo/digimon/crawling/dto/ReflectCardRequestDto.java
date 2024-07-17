package com.joo.digimon.crawling.dto;

import com.joo.digimon.global.enums.CardType;
import com.joo.digimon.global.enums.Color;
import com.joo.digimon.global.enums.Form;
import com.joo.digimon.global.enums.Rarity;
import lombok.Data;

import java.util.List;

@Data
public class ReflectCardRequestDto {
    Integer id;
    String cardNo;
    Rarity rarity;
    CardType cardType;
    Integer lv;
    Boolean isParallel;
    String cardName;
    Form form;
    String attribute;
    List<String> types;
    Integer dp;
    Integer playCost;
    Integer digivolveCost1;
    Integer digivolveCondition1;
    Integer digivolveCost2;
    Integer digivolveCondition2;
    String effect;
    String sourceEffect;
    String note;
    Color color1;
    Color color2;
    Color color3;
    String originUrl;
}
