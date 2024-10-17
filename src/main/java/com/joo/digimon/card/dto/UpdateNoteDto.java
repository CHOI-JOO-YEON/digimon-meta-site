package com.joo.digimon.card.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.joo.digimon.global.enums.CardOrigin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.Optional;

@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class UpdateNoteDto {

    Integer noteId;
    String name;
    LocalDate releaseDate;
    CardOrigin cardOrigin;
    Integer priority;
    Boolean isDisable;
}