package com.joo.digimon.card.dto.type;

import com.joo.digimon.card.model.CardCombineTypeEntity;
import com.joo.digimon.card.model.CardEntity;
import com.joo.digimon.card.model.TypeEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class TypeDto {
    Integer typeId;
    String name;
    String engName;
    String jpnName;
    Integer cardCount;
    List<String> cardNos;

    public TypeDto(TypeEntity typeEntity) {
        this.typeId = typeEntity.getId();
        this.name = typeEntity.getName();
        this.engName = typeEntity.getEngName();
        this.jpnName = typeEntity.getJpnName();
        this.cardCount = typeEntity.getCardCombineTypes().size();
        this.cardNos = typeEntity.getCardCombineTypes()
                .stream()
                .map(CardCombineTypeEntity::getCardEntity)
                .map(CardEntity::getCardNo)
                .collect(Collectors.toList());
    }
}
