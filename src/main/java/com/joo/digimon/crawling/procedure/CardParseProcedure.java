package com.joo.digimon.crawling.procedure;

import com.joo.digimon.crawling.dto.ReflectCardRequestDto;
import com.joo.digimon.global.enums.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public interface CardParseProcedure {

    default ReflectCardRequestDto execute() {
        ReflectCardRequestDto dto = new ReflectCardRequestDto();
        dto.setId(getId());
        dto.setCardNo(getCardNo());
        dto.setRarity(getRarity());
        dto.setCardType(getCardType());
        dto.setLv(getLv());
        dto.setIsParallel(isParallel());
        dto.setCardName(getCardName());
        dto.setForm(getForm());
        dto.setAttribute(getAttribute());
        dto.setTypes(getTypes());
        dto.setDp(getDp());
        dto.setPlayCost(getPlayCost());
        dto.setEffect(getEffect());
        dto.setSourceEffect(getSourceEffect());
        dto.setNote(getNote());
        dto.setColor1(getColor1());
        dto.setColor2(getColor2());
        dto.setColor3(getColor3());
        dto.setOriginUrl(getOriginUrl());
        dto.setLocale(getLocale());


        Digivolve digivolve1 = getDigivolve1();

        dto.setDigivolveCondition1(digivolve1.digivolveCondition);
        dto.setDigivolveCost1(digivolve1.digivolveCost);
        dto.setDigivolveCondition1(digivolve1.digivolveCondition);
        dto.setDigivolveCost1(digivolve1.digivolveCost);

        Digivolve digivolve2 = getDigivolve2();
        dto.setDigivolveCondition2(digivolve2.digivolveCondition);
        dto.setDigivolveCost2(digivolve2.digivolveCost);

        return dto;
    }

    Integer getId();

    String getCardNo();

    Rarity getRarity();

    CardType getCardType();

    Integer getLv();

    Boolean isParallel();

    String getCardName();

    Form getForm();

    String getAttribute();

    List<String> getTypes();

    Integer getDp();

    Integer getPlayCost();

    String getEffect();

    String getSourceEffect();

    String getNote();

    Color getColor1();

    Color getColor2();

    Color getColor3();

    String getOriginUrl();

    Locale getLocale();

    Digivolve getDigivolve1();

    Digivolve getDigivolve2();

    @Getter
    @AllArgsConstructor
    class Digivolve {
        Integer digivolveCost;
        Integer digivolveCondition;
    }
}
