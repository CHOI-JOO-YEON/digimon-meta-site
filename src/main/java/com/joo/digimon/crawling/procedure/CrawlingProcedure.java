package com.joo.digimon.crawling.procedure;

import com.joo.digimon.crawling.dto.CrawlingCardDto;
import com.joo.digimon.global.enums.Locale;
import org.jsoup.select.Elements;

import java.util.List;

public interface CrawlingProcedure {
    static String parseElementToPlainText(Elements select) {
        return select.html().replace("<br>\n", "");
    }

    default CrawlingCardDto execute() {
        CrawlingCardDto dto = new CrawlingCardDto();

        dto.setCardNo(getCardNo());
        dto.setRarity(getRarity());
        dto.setCardType(getCardType());
        dto.setLv(getLv());
        dto.setIsParallel(isParallel());
        dto.setCardName(getCardName());
        dto.setForm(getForm());
        dto.setAttribute(getAttribute());
        dto.setType(getType());
        dto.setDP(getDP());
        dto.setPlayCost(getPlayCost());
        dto.setDigivolveCost1(getDigivolveCost1());
        dto.setDigivolveCost2(getDigivolveCost2());
        dto.setEffect(getEffect());
        dto.setSourceEffect(getSourceEffect());
        dto.setNote(getNote());
        dto.setImgUrl(getImgUrl());
        dto.setLocale(getLocale());

        List<String> colors = getColors();
        dto.setColor1(colors.getFirst());
        if(colors.size() > 1) {
            dto.setColor2(colors.get(1));
        }
        if(colors.size() > 2) {
            dto.setColor3(colors.get(2));
        }


        return dto;
    }

    String getCardNo();
    String getRarity();
    String getCardType();
    String getLv();
    Boolean isParallel();
    String getCardName();
    String getForm();
    String getAttribute();
    String getType();
    String getDP();
    String getPlayCost();
    String getDigivolveCost1();
    String getDigivolveCost2();
    String getEffect();
    String getSourceEffect();
    String getNote();
    String getImgUrl();
    Locale getLocale();
    List<String> getColors();
}
