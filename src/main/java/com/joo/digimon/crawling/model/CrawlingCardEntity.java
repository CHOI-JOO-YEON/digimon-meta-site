package com.joo.digimon.crawling.model;


import com.joo.digimon.card.model.CardImgEntity;
import com.joo.digimon.card.model.ParallelCardImgEntity;
import com.joo.digimon.crawling.dto.CrawlingCardDto;
import com.joo.digimon.crawling.enums.CardType;
import com.joo.digimon.crawling.enums.Form;
import com.joo.digimon.crawling.enums.Rarity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "CRAWLING_CARDS_TB")
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

    @Column(unique = true)
    String imgUrl;

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
        this.imgUrl = dto.getImgUrl();
    }

    @OneToOne(mappedBy = "crawlingCardEntity")
    CardImgEntity cardImgEntity;

    @OneToOne(mappedBy = "crawlingCardEntity")
    ParallelCardImgEntity parallelCardImgEntity;
}
