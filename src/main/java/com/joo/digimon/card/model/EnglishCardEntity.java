package com.joo.digimon.card.model;

import com.joo.digimon.card.dto.card.CardAdminPutDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Optional;

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
    @JoinColumn(name = "cards_tb_id")
    CardEntity cardEntity;

    public void update(CardAdminPutDto dto) {
        Optional.ofNullable(dto.getEngEffect()).ifPresent(value -> this.effect = value);
        Optional.ofNullable(dto.getEngSourceEffect()).ifPresent(value -> this.sourceEffect = value);
        Optional.ofNullable(dto.getCardEngName()).ifPresent(value -> this.cardName = value);
    }


}
