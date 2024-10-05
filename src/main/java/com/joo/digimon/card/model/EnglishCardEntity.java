package com.joo.digimon.card.model;

import com.joo.digimon.card.dto.CardAdminRequestDto;
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

    public void update(CardAdminRequestDto dto) {
        this.effect = dto.getEngEffect();
        this.sourceEffect = dto.getEngSourceEffect();
        this.cardName = dto.getCardEngName();

    }


}
