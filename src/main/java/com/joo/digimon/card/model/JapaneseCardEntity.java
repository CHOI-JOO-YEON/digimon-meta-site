package com.joo.digimon.card.model;

import com.joo.digimon.card.dto.card.CardAdminPutDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Optional;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "JPN_CARDS_TB")
@NamedEntityGraph(
        name = "JapaneseCardEntity.detail",
        attributeNodes = {
                @NamedAttributeNode(value = "cardEntity")
        }
)
public class JapaneseCardEntity {
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
    @OneToOne
    @JoinColumn(name = "cards_tb_id")
    CardEntity cardEntity;

    public void update(CardAdminPutDto dto) {
        Optional.ofNullable(dto.getJpnEffect()).ifPresent(value -> this.effect = value);
        Optional.ofNullable(dto.getJpnSourceEffect()).ifPresent(value -> this.sourceEffect = value);
        Optional.ofNullable(dto.getCardJpnName()).ifPresent(value -> this.cardName = value);
    }


    public void updateCardEntity(CardEntity cardEntity) {
        this.cardEntity = cardEntity;
    }

    public void updateUploadUrl(String originalPrefix, String url) {
        this.uploadUrl = originalPrefix + url;
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
}
