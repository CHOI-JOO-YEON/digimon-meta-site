package com.joo.digimon.deck.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class FormatDeckCountResponseDto {
    List<FormatCount> allFormatCount;
    List<FormatCount> myFormatCount;

    @Data
    @AllArgsConstructor
    public static class FormatCount {
        Integer formatId;
        Integer count;
    }
}
