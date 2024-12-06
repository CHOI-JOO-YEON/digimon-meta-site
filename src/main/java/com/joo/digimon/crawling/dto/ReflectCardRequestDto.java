package com.joo.digimon.crawling.dto;

import com.joo.digimon.crawling.model.CrawlingCardEntity;
import com.joo.digimon.global.enums.*;
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
    Attribute attribute;
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
    Locale locale;

    CrawlingCardEntity crawlingCardEntity;
}
