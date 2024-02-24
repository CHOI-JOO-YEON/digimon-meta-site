package com.joo.digimon.deck.model;

import com.joo.digimon.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
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
                @NamedAttributeNode(value = "user")
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
    Integer id;

    @ManyToOne
    @JoinColumn(name = "users_tb_id")
    User user;

    @OneToMany(mappedBy = "deckEntity")
    Set<DeckCardEntity> deckCardEntities;

    String deckName;

    @CreationTimestamp
    private Timestamp createdDateTime;


//    @ManyToOne
//    @JoinColumn(name = "formats_tb_id")
//    Format format;

    public void addDeckCardEntity(DeckCardEntity deckCardEntity) {
        deckCardEntities.add(deckCardEntity);
    }
}
