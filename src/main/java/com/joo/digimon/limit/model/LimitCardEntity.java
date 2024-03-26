package com.joo.digimon.limit.model;

import com.joo.digimon.card.model.CardEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "LIMIT_CARDS_TB")
public class LimitCardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "limits_tb_id")
    LimitEntity limitEntity;

    @ManyToOne
    @JoinColumn(name = "cards_tb_id")
    CardEntity cardEntity;

    Integer allowedQuantity;
}
