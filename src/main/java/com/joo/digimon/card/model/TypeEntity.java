package com.joo.digimon.card.model;

import com.joo.digimon.card.dto.type.TypeDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "TYPES_TB")
@NamedEntityGraph(
        name = "TypeEntity.detail",
        attributeNodes = {
                @NamedAttributeNode(value = "cardCombineTypes", subgraph = "cardCombineTypeSubgraph"),

        },
        subgraphs = {
                @NamedSubgraph(
                        name = "cardCombineTypeSubgraph",
                        attributeNodes = {
                                @NamedAttributeNode("cardEntity")
                        }
                )
        }

)


public class TypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String name;
    String engName;
    String jpnName;

    @OneToMany(mappedBy = "typeEntity")
    List<CardCombineTypeEntity> cardCombineTypes;

    public void putType(TypeDto dto) {
        this.name = dto.getName();
        this.engName = dto.getEngName();
        this.jpnName = dto.getJpnName();
    }

    public void updateEngName(String engName) {
        this.engName = engName;
    }

    public void updateJpnName(String jpnName) {
        this.jpnName = jpnName;
    }
}
