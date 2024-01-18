package com.joo.digimon.crawling.dto;

import lombok.Data;

@Data
public class UpdateCrawlingRequestDto {
    Integer id;
    String cardNo;
    String rarity;
    String cardType;
    String lv;
    Boolean isParallel;
    String cardName;
    String form;
    String attribute;
    String type;
    String dP;
    String playCost;
    String digivolveCost1;
    String digivolveCost2;
    String effect;
    String sourceEffect;
    String note;
    String color1;
    String color2;
    String imgUrl;
}
