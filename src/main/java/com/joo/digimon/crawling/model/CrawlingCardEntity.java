package com.joo.digimon.crawling.model;


import com.joo.digimon.card.model.CardImgEntity;
import com.joo.digimon.card.model.ParallelCardImgEntity;
import com.joo.digimon.crawling.dto.CrawlingCardDto;
import com.joo.digimon.global.enums.Locale;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "CRAWLING_CARDS_TB")
@Setter
@ToString
public class CrawlingCardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(length = 1000)
    String effect;

    @Column(length = 1000)
    String sourceEffect;
    String note;
    String color1;
    String color2;
    String color3;

    String imgUrl;

    String errorMessage;
    Boolean isReflect;
    Boolean isEnCard;

    @Enumerated(EnumType.STRING)
    Locale locale;

    public static CrawlingCardEntity buildCrawlingCardEntity(CrawlingCardDto dto) {
        return CrawlingCardEntity.builder()
                .cardNo(dto.getCardNo())
                .rarity(dto.getRarity())
                .cardType(dto.getCardType())
                .lv(dto.getLv())
                .isParallel(dto.getIsParallel())
                .cardName(dto.getCardName())
                .form(dto.getForm())
                .attribute(dto.getAttribute())
                .type(dto.getType())
                .dP(dto.getDP())
                .playCost(dto.getPlayCost())
                .digivolveCost1(dto.getDigivolveCost1())
                .digivolveCost2(dto.getDigivolveCost2())
                .effect(dto.getEffect())
                .sourceEffect(dto.getSourceEffect())
                .note(dto.getNote())
                .color1(dto.getColor1())
                .color2(dto.getColor2())
                .color3(dto.getColor3())
                .imgUrl(dto.getImgUrl())
                .isReflect(false)
                .locale(dto.getLocale())
                .build();
    }

    public CrawlingCardEntity(CrawlingCardDto dto) {
        this.cardNo = dto.getCardNo();
        this.rarity = dto.getRarity();
        this.cardType = dto.getCardType();
        this.lv = dto.getLv();
        this.isParallel = dto.getIsParallel();
        this.cardName = dto.getCardName();
        this.form = dto.getForm();
        this.attribute = dto.getAttribute();
        this.type = dto.getType();
        this.dP = dto.getDP();
        this.playCost = dto.getPlayCost();
        this.digivolveCost1 = dto.getDigivolveCost1();
        this.digivolveCost2 = dto.getDigivolveCost2();
        this.effect = dto.getEffect();
        this.sourceEffect = dto.getSourceEffect();
        this.note = dto.getNote();
        this.color1 = dto.getColor1();
        this.color2 = dto.getColor2();
        this.color3 = dto.getColor3();
        this.imgUrl = dto.getImgUrl();
        this.isReflect = false;
        this.locale = dto.getLocale();
    }

    @OneToOne(mappedBy = "crawlingCardEntity")
    CardImgEntity cardImgEntity;

    @OneToOne(mappedBy = "crawlingCardEntity")
    ParallelCardImgEntity parallelCardImgEntity;

    public CrawlingCardEntity(CrawlingCardDto dto, boolean isEnCard) {
        this.cardNo = dto.getCardNo();
        this.rarity = dto.getRarity();
        this.cardType = dto.getCardType();
        this.lv = dto.getLv();
        this.isParallel = dto.getIsParallel();
        this.cardName = dto.getCardName();
        this.form = dto.getForm();
        this.attribute = dto.getAttribute();
        this.type = dto.getType();
        this.dP = dto.getDP();
        this.playCost = dto.getPlayCost();
        this.digivolveCost1 = dto.getDigivolveCost1();
        this.digivolveCost2 = dto.getDigivolveCost2();
        this.effect = dto.getEffect();
        this.sourceEffect = dto.getSourceEffect();
        this.note = dto.getNote();
        this.color1 = dto.getColor1();
        this.color2 = dto.getColor2();
        this.imgUrl = dto.getImgUrl();
        this.isReflect = false;
        this.isEnCard =isEnCard;
    }

    public void updateErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
