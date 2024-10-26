package com.joo.digimon.card.dto.type;

import com.joo.digimon.card.model.TypeEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TypeDto {
    Integer typeId;
    String name;
    String engName;
    Integer cardCount;

    public TypeDto(TypeEntity typeEntity) {
        this.typeId = typeEntity.getId();
        this.name = typeEntity.getName();
        this.engName = typeEntity.getEngName();
        this.cardCount = typeEntity.getCardCombineTypes().size();
    }
}
