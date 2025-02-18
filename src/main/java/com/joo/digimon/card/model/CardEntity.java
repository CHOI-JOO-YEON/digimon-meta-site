package com.joo.digimon.card.model;

import com.joo.digimon.global.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
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
    Color color3;
    @Enumerated(EnumType.STRING)
    Rarity rarity;
    @Enumerated(EnumType.STRING)
    CardType cardType;
    @Enumerated(EnumType.STRING)
    Form form;

    @Enumerated(EnumType.STRING)
    Attribute attribute;

    @OneToMany(mappedBy = "cardEntity")
    Set<CardImgEntity> cardImgEntity;

    @OneToMany(mappedBy = "cardEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<CardCombineTypeEntity> cardCombineTypeEntities;

    String sortString;

    LocalDate releaseDate;

    Boolean isOnlyEnCard;

    @OneToOne(mappedBy = "cardEntity")
    EnglishCardEntity englishCard;

    @OneToOne(mappedBy = "cardEntity")
    JapaneseCardEntity japaneseCardEntity;

    public void updateEnglishCard(EnglishCardEntity englishCard) {
        this.englishCard = englishCard;
    }

    public void updateJapaneseCard(JapaneseCardEntity japaneseCard) {
        this.japaneseCardEntity = japaneseCard;
    }
    public void updateCardCombineTypes(Set<CardCombineTypeEntity> newCardCombineTypeEntities) {
        if (this.cardCombineTypeEntities != null) {
            this.cardCombineTypeEntities.clear();
        } else {
            this.cardCombineTypeEntities = new HashSet<>();
        }
        if (newCardCombineTypeEntities != null) {
            for (CardCombineTypeEntity entity : newCardCombineTypeEntities) {
                addCardCombineType(entity);
            }
        }
    }

    public void updateCardName(String cardName) {
        this.cardName = cardName;
    }

    public void updateEffect(String effect) {
        this.effect = effect;
    }

    public void updateSourceEffect(String sourceEffect) {
        this.sourceEffect = sourceEffect;
    }

    public void updateAttribute(Attribute attribute) {
        this.attribute = attribute;
    }
    public void updateForm(Form form) {
        this.form = form;
    }
    public void updateIsOnlyEnCard(Boolean isOnlyEnCard) {
        this.isOnlyEnCard = isOnlyEnCard;
    }

    public void addCardCombineType(CardCombineTypeEntity entity) {
        cardCombineTypeEntities.add(entity);
        entity.setCardEntity(this);
    }
}
