package com.joo.digimon.card.dto.use_card;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UseCardResponseDto {
    Integer totalCount;
    List<UseCard> useCardList;
}
