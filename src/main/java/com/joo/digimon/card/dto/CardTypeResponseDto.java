package com.joo.digimon.card.dto;

import com.joo.digimon.card.model.TypeEntity;
import lombok.Data;

@Data
public class CardTypeResponseDto {
    String name;
    Integer id;

    public CardTypeResponseDto(TypeEntity typeEntity) {
        this.name = typeEntity.getName();
        this.id = typeEntity.getId();
    }
}
