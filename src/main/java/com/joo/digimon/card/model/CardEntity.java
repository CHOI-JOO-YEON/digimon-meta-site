package com.joo.digimon.card.model;

import com.joo.digimon.crawling.enums.CardType;
import com.joo.digimon.crawling.enums.Color;
import com.joo.digimon.crawling.enums.Form;
import com.joo.digimon.crawling.enums.Rarity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "CARDS_TB")
public class CardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(unique = true)
    String cardNo;

    Integer lv;
    String cardName;
    String attribute;

    Integer dp;
    Integer playCost;
    Integer digivolveCost1;
    Integer digivolveCondition1;
    Integer digivolveCost2;
    Integer digivolveCondition2;

    @Column(length = 1000)
    String effect;
    @Column(length = 1000)
    String sourceEffect;

    @Enumerated(EnumType.STRING)
    Color color1;
    @Enumerated(EnumType.STRING)
    Color color2;
    @Enumerated(EnumType.STRING)
    Rarity rarity;
    @Enumerated(EnumType.STRING)
    CardType cardType;
    @Enumerated(EnumType.STRING)
    Form form;

    @OneToMany(mappedBy = "cardEntity")
    Set<CardImgEntity> cardImgEntity;

    @OneToMany(mappedBy = "cardEntity")
    Set<CardCombineTypeEntity> cardCombineTypeEntities;

    String sortString;

    LocalDate releaseDate;
}
