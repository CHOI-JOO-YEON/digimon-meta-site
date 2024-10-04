package com.joo.digimon.card.model;

import com.joo.digimon.card.dto.UpdateNoteDto;
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

    public void update(UpdateNoteDto dto) {
        if(dto.getName()!=null) {
            this.name = dto.getName();
        }
        if(dto.getReleaseDate()!=null) {
            this.releaseDate = dto.getReleaseDate();
        }
        if(dto.getCardOrigin()!=null) {
            this.cardOrigin = dto.getCardOrigin();
        }
        if(dto.getPriority()!=null) {
            this.priority = dto.getPriority();
        }
    }

}
