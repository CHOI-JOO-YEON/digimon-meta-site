package com.joo.digimon.deck.model;

import com.joo.digimon.global.enums.Color;
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
@Table(name = "DECK_COLORS_TB")
public class DeckColor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "decks_tb_id")
    private DeckEntity deckEntity;

    @Enumerated(EnumType.STRING)
    private Color color;
}
