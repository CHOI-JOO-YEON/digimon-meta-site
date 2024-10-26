package com.joo.digimon.card.model;

import com.joo.digimon.card.dto.note.UpdateNoteDto;
import com.joo.digimon.global.enums.CardOrigin;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "NOTES_TB")
@NamedEntityGraph(
        name = "NoteEntity.byCardImg",
        attributeNodes = {
                @NamedAttributeNode(value = "cardImgEntities")
        }
)
public class NoteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String name;
    LocalDate releaseDate;

    @Enumerated(EnumType.STRING)
    CardOrigin cardOrigin;

    @OneToMany(mappedBy = "noteEntity")
    Set<CardImgEntity> cardImgEntities;

    @OneToMany(mappedBy = "noteEntity")
    Set<ParallelCardImgEntity> parallelCardImgEntities;

    Boolean isDisable;

    Integer priority;

    public void putNote(UpdateNoteDto dto) {
        this.name = dto.getName();
        this.releaseDate = dto.getReleaseDate();
        this.cardOrigin = dto.getCardOrigin();
        this.priority = dto.getPriority();
        this.isDisable = dto.getIsDisable();
    }

}
