package com.joo.digimon.card.dto;

import com.joo.digimon.card.model.TypeEntity;
import lombok.Data;

@Data
public class TypeDto {
    Integer id;
    String name;
    String engName;
    Integer cardCount;

    public TypeDto(TypeEntity typeEntity) {
        this.id = typeEntity.getId();
        this.name = typeEntity.getName();
        this.engName = typeEntity.getEngName();
        this.cardCount = typeEntity.getCardCombineTypes().size();
    }
}
