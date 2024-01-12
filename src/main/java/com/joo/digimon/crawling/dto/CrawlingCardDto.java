package com.joo.digimon.crawling.dto;

import com.joo.digimon.crawling.enums.CardType;
import com.joo.digimon.crawling.enums.Color;
import com.joo.digimon.crawling.enums.Form;
import com.joo.digimon.crawling.enums.Rarity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class CrawlingCardDto {

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
