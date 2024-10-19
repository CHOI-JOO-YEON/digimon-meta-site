package com.joo.digimon.deck.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FormatRequestDto {
    String formatName;
    LocalDate startDate;
    LocalDate endDate;
    Boolean isOnlyEn;
}
