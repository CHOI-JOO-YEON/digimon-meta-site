package com.joo.digimon.crawling.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.joo.digimon.crawling.model.CrawlingCardEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CrawlingCardDto {
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
        String color3;
        String imgUrl;
        String errorMessage;
        String locale;

    public CrawlingCardDto(CrawlingCardEntity crawlingCardEntity) {
        this.id = crawlingCardEntity.getId();
        this.cardNo = crawlingCardEntity.getCardNo();
        this.rarity = crawlingCardEntity.getRarity();
        this.cardType = crawlingCardEntity.getCardType();
        this.lv = crawlingCardEntity.getLv();
        this.isParallel = crawlingCardEntity.getIsParallel();
        this.cardName = crawlingCardEntity.getCardName();
        this.form = crawlingCardEntity.getForm();
        this.attribute = crawlingCardEntity.getAttribute();
        this.type = crawlingCardEntity.getType();
        this.dP = crawlingCardEntity.getDP();
        this.playCost = crawlingCardEntity.getPlayCost();
        this.digivolveCost1 = crawlingCardEntity.getDigivolveCost1();
        this.digivolveCost2 = crawlingCardEntity.getDigivolveCost2();
        this.effect = crawlingCardEntity.getEffect();
        this.sourceEffect = crawlingCardEntity.getSourceEffect();
        this.note = crawlingCardEntity.getNote();
        this.color1 = crawlingCardEntity.getColor1();
        this.color2 = crawlingCardEntity.getColor2();
        this.imgUrl = crawlingCardEntity.getImgUrl();
        this.errorMessage = crawlingCardEntity.getErrorMessage();
        this.locale = crawlingCardEntity.getLocale();
    }

}
