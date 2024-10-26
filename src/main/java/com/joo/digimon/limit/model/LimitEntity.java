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
public class LimitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(nullable = false)
    @NotNull
    LocalDate restrictionBeginDate;

    @OneToMany(mappedBy = "limitEntity")
    Set<LimitCardEntity> limitCardEntities;

    public void update(LimitPutRequestDto limitPutRequestDto) {
        this.restrictionBeginDate = limitPutRequestDto.getRestrictionBeginDate();
    }
}
