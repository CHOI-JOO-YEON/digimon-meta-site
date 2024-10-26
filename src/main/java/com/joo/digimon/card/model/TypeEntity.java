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
                @NamedAttributeNode(value = "cardCombineTypes")
        }
)


public class TypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String name;
    String engName;

    @OneToMany(mappedBy = "typeEntity")
    List<CardCombineTypeEntity> cardCombineTypes;

    public void putType(TypeDto dto) {
        this.name = dto.getName();
        this.engName = dto.getEngName();
    }
}
