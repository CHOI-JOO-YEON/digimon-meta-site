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
@Table(name = "limit_pair_tb")
public class LimitPairEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(mappedBy = "pairA", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LimitPairCardEntity> pairACardSet;

    @OneToMany(mappedBy = "pairB", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LimitPairCardEntity> pairBCardSet;

    @ManyToOne
    @JoinColumn(name = "limit_entity", nullable = false)
    private LimitEntity limitEntity;
}
