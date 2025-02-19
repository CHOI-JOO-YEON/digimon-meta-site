package com.joo.digimon.limit.model;

import com.joo.digimon.limit.dto.LimitPutRequestDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "limit_entity")
public class LimitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(nullable = false)
    @NotNull
    LocalDate restrictionBeginDate;

    @OneToMany(mappedBy = "limitEntity")
    Set<LimitCardEntity> limitCardEntities;

    @OneToMany(mappedBy = "limitEntity")
    Set<LimitPairEntity> limitPairEntities;
    
    public void updateBeginDate(LocalDate newRestrictionBeginDate) {
        this.restrictionBeginDate = newRestrictionBeginDate;
    }
}
