package com.joo.digimon.card.dto;

import com.joo.digimon.card.model.NoteEntity;
import com.joo.digimon.global.enums.CardOrigin;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ResponseNoteDto {
    Integer noteId;
    String name;
    LocalDate releaseDate;
    CardOrigin cardOrigin;
    Integer priority;
    Integer cardCount;
    Boolean isDisable;

    public ResponseNoteDto(NoteEntity noteEntity) {
        this.noteId = noteEntity.getId();
        this.name = noteEntity.getName();
        this.releaseDate = noteEntity.getReleaseDate();
        this.cardOrigin = noteEntity.getCardOrigin();
        this.priority = noteEntity.getPriority();
        if (noteEntity.getCardImgEntities() != null) {
            this.cardCount = noteEntity.getCardImgEntities().size();
        } else {
            this.cardCount = 0;
        }
        this.isDisable = noteEntity.getIsDisable();


    }
}
