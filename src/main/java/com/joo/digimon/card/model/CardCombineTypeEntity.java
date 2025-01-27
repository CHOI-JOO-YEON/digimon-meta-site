package com.joo.digimon.card.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "CARD_TYPES_TB")
public class CardCombineTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "cards_tb_id")
    CardEntity cardEntity;


    @ManyToOne
    @JoinColumn(name = "types_tb_id")
    TypeEntity typeEntity;

    public void updateType(TypeEntity typeEntity) {
        this.typeEntity = typeEntity;
    }

    void setCardEntity(CardEntity cardEntity) {
        this.cardEntity = cardEntity;
    }
}
