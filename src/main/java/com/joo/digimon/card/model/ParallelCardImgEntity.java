package com.joo.digimon.card.model;

import com.joo.digimon.crawling.model.CrawlingCardEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "PARALLEL_CARDS_IMG_TB")
@NamedEntityGraph(name = "ParallelCardImgEntity.detail",
        attributeNodes = {@NamedAttributeNode("cardEntity")}
)
public class ParallelCardImgEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "cards_tb_id")
    CardEntity cardEntity;

    String originUrl;
    String uploadUrl;

    @OneToOne
    @JoinColumn(name = "crawling_cards_tb_id")
    CrawlingCardEntity crawlingCardEntity;

    @ManyToOne
    @JoinColumn(name = "note_tb_id")
    NoteEntity noteEntity;

    public void updateUploadUrl(String url) {
        this.uploadUrl = url;
    }
}
