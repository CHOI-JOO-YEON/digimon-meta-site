package com.joo.digimon.crawling.model;


import com.joo.digimon.card.model.CardEntity;
import com.joo.digimon.card.model.NoteEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "DELETED_EN_CARDS_IMG_TB")
public class DeletedEnCardImg {
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

}
