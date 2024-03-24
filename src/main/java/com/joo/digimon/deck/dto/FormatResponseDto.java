package com.joo.digimon.deck.dto;

import com.joo.digimon.deck.model.Format;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FormatResponseDto {
    Integer id;
    String formatName;
    LocalDate startDate;
    LocalDate endDate;
    Boolean isOnlyEn;

    public FormatResponseDto(Format format) {
        this.id = format.getId();
        this.formatName = format.getName();
        this.startDate = format.getStartDate();
        this.endDate = format.getEndDate();
        this.isOnlyEn=format.getIsOnlyEn();
    }
}
