package com.joo.digimon.card.dto;

import com.joo.digimon.global.enums.CardOrigin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateNoteDto {

    Integer noteId;
    String name;
    LocalDate releaseDate;
    CardOrigin cardOrigin;
    Integer priority;
}
