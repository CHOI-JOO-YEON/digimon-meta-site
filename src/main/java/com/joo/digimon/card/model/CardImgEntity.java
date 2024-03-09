package com.joo.digimon.card.model;

import com.joo.digimon.crawling.model.CrawlingCardEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "CARDS_IMG_TB")
@NamedEntityGraph(
        name = "CardImgEntity.detail",
        attributeNodes = {
                @NamedAttributeNode(value = "cardEntity", subgraph = "cardEntitySubgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "cardEntitySubgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "cardCombineTypeEntities",subgraph = "typeSubgraph"),
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
@ToString
public class CardImgEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "cards_tb_id")
    CardEntity cardEntity;

    String originUrl;
    String uploadUrl;
    String smallImgUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crawling_cards_tb_id")
    CrawlingCardEntity crawlingCardEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_tb_id")
    NoteEntity noteEntity;

    Boolean isParallel;

    public void updateUploadUrl(String originalPrefix, String smallPrefix, String url) {
        this.uploadUrl = originalPrefix+url;
        this.smallImgUrl= smallPrefix+url;
    }
}
