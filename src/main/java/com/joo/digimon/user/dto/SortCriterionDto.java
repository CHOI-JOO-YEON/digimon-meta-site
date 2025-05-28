package com.joo.digimon.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SortCriterionDto {
    private String field;
    private Boolean ascending;
    private Map<String, Integer> orderMap;
}
