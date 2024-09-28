package com.joo.digimon.card.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "ENG_CARDS_TB")
public class EnglishCardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String cardName;
    @Column(length = 1000)
    String effect;
    @Column(length = 1000)
    String sourceEffect;
    LocalDate releaseDate;

    @OneToOne
    CardEntity card;

}
