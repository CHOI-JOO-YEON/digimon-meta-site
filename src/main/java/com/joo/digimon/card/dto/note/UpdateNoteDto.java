package com.joo.digimon.card.dto.note;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.joo.digimon.global.enums.CardOrigin;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class UpdateNoteDto {

    Integer noteId;
    String name;
    LocalDate releaseDate;
    CardOrigin cardOrigin;
    Integer priority;
    Boolean isDisable;
    String description;
    String parent;
}