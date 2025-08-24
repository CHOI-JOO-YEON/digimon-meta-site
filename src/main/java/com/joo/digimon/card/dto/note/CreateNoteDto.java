package com.joo.digimon.card.dto.note;

import com.joo.digimon.global.enums.CardOrigin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateNoteDto {

    @NotNull
    String name;
    LocalDate releaseDate;
    CardOrigin cardOrigin;
    Integer priority;
    String description;
    String parent;
}
