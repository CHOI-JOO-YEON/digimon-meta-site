package com.joo.digimon.deck.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeckImportRequestDto {
    Map<String,Integer> deck;
}
