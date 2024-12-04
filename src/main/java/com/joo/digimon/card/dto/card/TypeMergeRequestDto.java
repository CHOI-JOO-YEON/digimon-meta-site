package com.joo.digimon.card.dto.card;

import com.joo.digimon.global.enums.Locale;
import lombok.Data;

@Data
public class TypeMergeRequestDto {
    Integer baseTypeId;
    Integer targetTypeId;
    Locale locale;
}
