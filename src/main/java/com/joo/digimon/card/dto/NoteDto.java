package com.joo.digimon.card.dto;

import com.joo.digimon.card.model.NoteEntity;
import lombok.Data;

@Data
public class NoteDto {
    Integer noteId;
    String name;

    public NoteDto(NoteEntity noteEntity) {
        this.noteId=noteEntity.getId();
        this.name = noteEntity.getName();

    }
}
