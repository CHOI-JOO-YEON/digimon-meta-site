package com.joo.digimon.crawling.model;


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
public class CrawlingCardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String cardNo;

    @Enumerated(EnumType.STRING)
    Rarity rarity;
    @Enumerated(EnumType.STRING)
    CardType cardType;

    String lv;

    @NonNull
    Boolean isParallel;
    @NonNull
    String cardName;
    @Enumerated(EnumType.STRING)
    Form form;
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
