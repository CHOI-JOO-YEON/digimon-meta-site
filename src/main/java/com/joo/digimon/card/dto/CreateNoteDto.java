package com.joo.digimon.card.dto;

import com.joo.digimon.card.model.NoteEntity;
import com.joo.digimon.global.enums.CardOrigin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
public class CreateNoteDto {

    @NotNull
    String name;
    LocalDate releaseDate;
    CardOrigin cardOrigin;
    Integer priority;
}
