package com.joo.digimon.limit.model;


import com.joo.digimon.card.model.CardEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "limit_pair_cards_tb")
public class LimitPairCardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cards_tb_id", nullable = false)
    private CardEntity cardEntity;

    @ManyToOne
    @JoinColumn(name = "pair_a_id")
    private LimitPairEntity pairA;

    @ManyToOne
    @JoinColumn(name = "pair_b_id")
    private LimitPairEntity pairB;
}

