package com.joo.digimon.card.dto.card;

import com.joo.digimon.global.enums.Attribute;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TraitDto {
    private final List<TraitDtoForm> forms;
    private final List<String> attributes;
    private final List<TraitDtoType> types;

    public record TraitDtoForm(String id, String displayName) {}
    public record TraitDtoType(String name, Integer id) {}
}
