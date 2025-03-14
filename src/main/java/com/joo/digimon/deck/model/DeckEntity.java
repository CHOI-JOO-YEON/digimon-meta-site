package com.joo.digimon.deck.model;

import com.joo.digimon.card.model.CardEntity;
import com.joo.digimon.global.enums.CardType;
import com.joo.digimon.deck.dto.RequestDeckDto;
import com.joo.digimon.user.model.User;
import com.joo.digimon.util.SpecialLimitCard;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
@NamedEntityGraph(
        name = "Deck.summary",
        attributeNodes = {
                @NamedAttributeNode(value = "deckCardEntities"),
                @NamedAttributeNode(value = "user"), @NamedAttributeNode(value = "format"), @NamedAttributeNode(value = "deckColors")
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

    private Timestamp updateTimestamp;

    private Boolean isPublic;


    private Boolean isValid;




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
        this.updateTimestamp= Timestamp.valueOf(LocalDateTime.now());
    }

    public void addDeckColor(DeckColor deckColor){
        if(this.deckColors == null){
            this.deckColors = new HashSet<>();
        }
        deckColors.add(deckColor);
    }

    public void updateDeckValid() {
        Map<CardEntity, Integer> cardCountMap = new HashMap<>();
        int deckCount = 0;
        int tamaCount = 0;

        for (DeckCardEntity deckCardEntity : deckCardEntities) {
            CardEntity card = deckCardEntity.getCardImgEntity().getCardEntity();
            cardCountMap.put(card, cardCountMap.getOrDefault(card, 0) + deckCardEntity.cnt);
            if (card.getCardType().equals(CardType.DIGITAMA)) {

                tamaCount+=deckCardEntity.cnt;
                continue;
            }
            deckCount+=deckCardEntity.cnt;
        }
        if (deckCount != 50) {
            isValid = false;
            return;
        }

        if (tamaCount > 5) {
            isValid = false;
            return;
        }

        for (Map.Entry<CardEntity, Integer> cardEntry : cardCountMap.entrySet()) {
            if (cardEntry.getValue() > SpecialLimitCard.getCardLimit(cardEntry.getKey().getCardNo())) {
                isValid = false;
                return;
            }
        }
        isValid=true;
    }
}
