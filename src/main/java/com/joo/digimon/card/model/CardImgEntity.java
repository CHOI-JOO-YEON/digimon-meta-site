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
@Table(name = "CARDS_IMG_TB")
public class CardImgEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @OneToOne
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
}
