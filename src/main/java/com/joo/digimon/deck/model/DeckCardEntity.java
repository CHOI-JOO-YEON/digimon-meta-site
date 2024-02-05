package com.joo.digimon.deck.model;

import com.joo.digimon.card.model.CardImgEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "DECKS_CARDS_TB")
public class DeckCardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;


    @ManyToOne
    @JoinColumn(name = "cards_img_tb_id")
    CardImgEntity cardImgEntity;

    @ManyToOne
    @JoinColumn(name = "decks_tb_id")
    DeckEntity deckEntity;

    Integer cnt;

}
