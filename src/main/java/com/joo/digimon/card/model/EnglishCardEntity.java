package com.joo.digimon.card.model;

import com.joo.digimon.card.dto.card.CardAdminPutDto;
import com.joo.digimon.crawling.dto.ReflectCardRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Optional;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "ENG_CARDS_TB")
@NamedEntityGraph(
        name = "EnglishCardEntity.detail",
        attributeNodes = {
                @NamedAttributeNode(value = "cardEntity")
        }
)
public class EnglishCardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String cardName;
    @Column(length = 1000)
    String effect;
    @Column(length = 1000)
    String sourceEffect;
    String originUrl;
    String uploadUrl;
    String webpUrl;

    String sampleWebpUrl;

    @OneToOne
    @JoinColumn(name = "cards_tb_id")
    CardEntity cardEntity;

    public void update(CardAdminPutDto dto) {
        Optional.ofNullable(dto.getEngEffect()).ifPresent(value -> this.effect = value);
        Optional.ofNullable(dto.getEngSourceEffect()).ifPresent(value -> this.sourceEffect = value);
        Optional.ofNullable(dto.getCardEngName()).ifPresent(value -> this.cardName = value);
    }


    public void updateCardEntity(CardEntity cardEntity) {
        this.cardEntity = cardEntity;
    }

    public void updateUploadUrl(String prefix, String key) {
        this.uploadUrl = prefix + key;
    }
    public void updateWebpUrl(String originalPrefix, String key, String webpPrefix) {
        this.webpUrl = webpPrefix + originalPrefix + key;
    }

    public void updateUploadUrl(String url) {
        this.uploadUrl = url;
    }
    public void updateOriginUrl(String url) {
        this.originUrl = url;
    }

    public void update(ReflectCardRequestDto dto) {
        effect = dto.getEffect();
        sourceEffect = dto.getSourceEffect();
        cardName = dto.getCardName();
        originUrl = dto.getOriginUrl();
    }
}
