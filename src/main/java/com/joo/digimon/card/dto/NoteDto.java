package com.joo.digimon.card.dto;

import com.joo.digimon.card.model.NoteEntity;
import com.joo.digimon.global.enums.CardOrigin;
import lombok.Data;

import java.time.LocalDate;

@Data
public class NoteDto {
    Integer noteId;
    String name;
    LocalDate releaseDate;
    CardOrigin cardOrigin;
    Integer priority;
    public NoteDto(NoteEntity noteEntity) {
        this.noteId=noteEntity.getId();
        this.name = noteEntity.getName();
        this.releaseDate = noteEntity.getReleaseDate();
        this.cardOrigin = noteEntity.getCardOrigin();
        this.priority=noteEntity.getPriority();
    }
}
