package com.joo.digimon.crawling.dto;

import lombok.Data;

@Data
public class ReflectCardRequestDto {
    Integer id;

    String cardNo;
    String rarity;
    String cardType;
    Integer lv;
    Boolean isParallel;
    String cardName;
    String form;
    String attribute;
    String type;
    Integer dP;
    Integer playCost;
    Integer digivolveCost1;
    Integer digivolveCondition1;
    Integer digivolveCost2;
    Integer digivolveCondition2;
    String effect;
    String sourceEffect;
    String note;

    String color1;
    String color2;
    String originUrl;
}
