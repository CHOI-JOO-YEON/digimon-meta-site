package com.joo.digimon.card.model;

import com.joo.digimon.card.dto.card.CardAdminPutDto;
import com.joo.digimon.crawling.model.CrawlingCardEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "CARDS_IMG_TB")
@NamedEntityGraph(
        name = "CardImgEntity.detail",
        attributeNodes = {
                @NamedAttributeNode(value = "cardEntity", subgraph = "cardEntitySubgraph"),
                @NamedAttributeNode(value = "noteEntity")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "cardEntitySubgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "englishCard"),
                                @NamedAttributeNode(value = "japaneseCardEntity"),
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
    Boolean isEnCard;
    Boolean isJpnCard;

    private LocalDateTime modifiedAt;

    public void updateUploadUrl(String originalPrefix, String smallPrefix, String url) {
        this.uploadUrl = originalPrefix + url;
        this.smallImgUrl = smallPrefix + url;
    }

    public void update(CardAdminPutDto dto) {
        this.cardEntity.cardNo = dto.getCardNo();
        this.cardEntity.cardName = dto.getCardName();
        this.cardEntity.lv = dto.getLv();
        this.cardEntity.dp = dto.getDp();
        this.cardEntity.playCost = dto.getPlayCost();
        this.cardEntity.digivolveCost1 = dto.getDigivolveCost1();
        this.cardEntity.digivolveCondition1 = dto.getDigivolveCondition1();
        this.cardEntity.digivolveCost2 = dto.getDigivolveCost2();
        this.cardEntity.digivolveCondition2 = dto.getDigivolveCondition2();
        this.cardEntity.effect = dto.getEffect();
        this.cardEntity.sourceEffect = dto.getSourceEffect();
        this.cardEntity.color1 = dto.getColor1();
        this.cardEntity.color2 = dto.getColor2();
        this.cardEntity.color3 = dto.getColor3();
        this.cardEntity.rarity = dto.getRarity();
        this.cardEntity.cardType = dto.getCardType();
        this.cardEntity.form = dto.getForm();
        this.cardEntity.attribute = dto.getAttribute();
        this.cardEntity.releaseDate = dto.getReleaseDate();
        this.isEnCard = dto.getIsEn();
        this.modifiedAt = LocalDateTime.now();
    }
    public void updateType(Set<CardCombineTypeEntity> cardCombineTypeEntityList){
        this.cardEntity.cardCombineTypeEntities = cardCombineTypeEntityList;
    }

    public void updateNote(NoteEntity noteEntity) {
        this.noteEntity=noteEntity;
    }

    public void updateCrawlingCardEntity(CrawlingCardEntity crawlingCardEntity) {
        this.crawlingCardEntity = crawlingCardEntity;
    }

    public void updateOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }

    public void updateIsEnCard(boolean b) {
        this.isEnCard = b;
    }

    public void updateIsJpnCard(boolean b) {
        this.isJpnCard = b;
    }
}
