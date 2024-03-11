package com.joo.digimon.deck.model;

import com.joo.digimon.deck.dto.RequestDeckDto;
import com.joo.digimon.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "DECKS_TB")
@NamedEntityGraph(
        name = "Deck.detail",
        attributeNodes = {
                @NamedAttributeNode(value = "deckCardEntities", subgraph = "deckCardSubgraph"),
                @NamedAttributeNode(value = "user"),@NamedAttributeNode(value = "format"), @NamedAttributeNode(value = "deckColors")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "deckCardSubgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "cardImgEntity", subgraph = "cardImgSubgraph"),
                        }
                ),
                @NamedSubgraph(
                        name = "cardImgSubgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "cardEntity", subgraph = "cardEntitySubgraph"),
                        }
                ),
                @NamedSubgraph(
                        name = "cardEntitySubgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "cardCombineTypeEntities", subgraph = "typeSubgraph"),
                        }
                ),
                @NamedSubgraph(
                        name = "typeSubgraph",
                        attributeNodes = {
                                @NamedAttributeNode("typeEntity")
                        }
                )
        }
)

public class DeckEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "users_tb_id")
    private User user;

    @OneToMany(mappedBy = "deckEntity")
    Set<DeckCardEntity> deckCardEntities;


    @OneToMany(mappedBy = "deckEntity")
    Set<DeckColor> deckColors;

    private String deckName;

    @CreationTimestamp
    private Timestamp createdDateTime;

    private Boolean isPublic;


    @ManyToOne
    @JoinColumn(name = "formats_tb_id")
    Format format;

    public void addDeckCardEntity(DeckCardEntity deckCardEntity) {
        deckCardEntities.add(deckCardEntity);
    }
    public void updateDeckMetaData(RequestDeckDto requestDeckDto, Format format) {
        this.deckName = requestDeckDto.getDeckName();
        this.isPublic = requestDeckDto.getIsPublic();
        this.format = format;
    }

    public void addDeckColor(DeckColor deckColor){
        if(this.deckColors == null){
            this.deckColors = new HashSet<>();
        }
        deckColors.add(deckColor);
    }
}
