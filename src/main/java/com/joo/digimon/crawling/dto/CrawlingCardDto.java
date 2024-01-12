package com.joo.digimon.crawling.dto;

import com.joo.digimon.crawling.enums.CardType;
import com.joo.digimon.crawling.enums.Rarity;
import lombok.Data;
import lombok.NonNull;

@Data
public class CrawlingCardDto {

    @NonNull
    String cardNo;
    @NonNull
    Rarity rarity;
    @NonNull
    CardType cardType;

    String lv;

    @NonNull
    Boolean isParallel;
    @NonNull
    String cardName;
    String form;
    String attribute;
    String type;
    Integer dP;
    Integer playCost;
    String digivolveCost1;
    String digivolveCost2;
    String effect;
    String sourceEffect;
    String note;

    @NonNull
    String imgUrl;





}
