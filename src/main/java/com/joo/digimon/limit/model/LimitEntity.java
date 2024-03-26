package com.joo.digimon.limit.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@NamedEntityGraph(
        name = "LimitEntity.detail",
        attributeNodes = {
                @NamedAttributeNode(value = "limitCardEntities", subgraph = "cardEntitySubgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "cardEntitySubgraph",
                        attributeNodes = {
                                @NamedAttributeNode("cardEntity")
                        }

                ),
        }
)
public class LimitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    LocalDate restrictionBeginDate;

    @OneToMany(mappedBy = "limitEntity")
    Set<LimitCardEntity> limitCardEntities;
}
