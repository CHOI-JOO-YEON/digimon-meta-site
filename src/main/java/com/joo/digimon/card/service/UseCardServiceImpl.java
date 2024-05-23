package com.joo.digimon.card.service;

import com.joo.digimon.card.dto.CardVo;
import com.joo.digimon.card.dto.UseCardResponseDto;
import com.joo.digimon.card.model.QCardEntity;
import com.joo.digimon.card.model.QCardImgEntity;
import com.joo.digimon.deck.model.QDeckCardEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UseCardServiceImpl implements UseCardService {

    @Value("${domain.url}")
    private String prefixUrl;
    private final EntityManager entityManager;

    @Override
    public List<UseCardResponseDto> findTopUsedCardsWithACard(Integer cardImgId, Integer formatId) {
        QCardImgEntity cardImg = QCardImgEntity.cardImgEntity;
        QCardEntity card = QCardEntity.cardEntity;
        QDeckCardEntity deckCard = QDeckCardEntity.deckCardEntity;
        QDeckCardEntity subDeckCard = new QDeckCardEntity("subDeckCard");

        Long totalCount = getTotalDeckCountWithCard(cardImgId, formatId);

        List<Tuple> results = getTopUsedCardsWithCardQuery(cardImgId, formatId, cardImg, card, deckCard, subDeckCard)
                .fetch();

        return convertToResponseDtos(results, totalCount);
    }

    private Long getTotalDeckCountWithCard(Integer cardImgId, Integer formatId) {
        QDeckCardEntity deckCard = QDeckCardEntity.deckCardEntity;

        return new JPAQuery<>(entityManager)
                .select(deckCard.deckEntity.id.countDistinct())
                .from(deckCard)
                .where(deckCard.cardImgEntity.id.eq(cardImgId),
                        deckCard.deckEntity.format.id.eq(formatId),
                        deckCard.deckEntity.isValid.isTrue())
                .fetchOne();
    }

    private JPAQuery<Tuple> getTopUsedCardsWithCardQuery(Integer cardImgId, Integer formatId,
                                                         QCardImgEntity cardImg, QCardEntity card,
                                                         QDeckCardEntity deckCard, QDeckCardEntity subDeckCard) {
        return new JPAQuery<>(entityManager)
                .select(card.id, card.cardName, deckCard.deckEntity.id.countDistinct().as("usedCount"), cardImg)
                .from(deckCard)
                .join(deckCard.cardImgEntity, cardImg)
                .join(cardImg.cardEntity, card)
                .join(subDeckCard).on(subDeckCard.deckEntity.eq(deckCard.deckEntity))
                .where(subDeckCard.cardImgEntity.id.eq(cardImgId),
                        cardImg.id.ne(cardImgId),
                        deckCard.deckEntity.format.id.eq(formatId),
                        deckCard.deckEntity.isValid.isTrue())
                .groupBy(card.id)
                .orderBy(Expressions.numberPath(Long.class, "usedCount").desc())
                .limit(10);
    }

    private List<UseCardResponseDto> convertToResponseDtos(List<Tuple> results, Long totalCount) {
        List<UseCardResponseDto> responseDtos = new ArrayList<>();
        int rank = 1;

        for (Tuple tuple : results) {
            UseCardResponseDto responseDto = new UseCardResponseDto();
            responseDto.setRank(rank++);
            responseDto.setCard(new CardVo(Objects.requireNonNull(tuple.get(QCardImgEntity.cardImgEntity)), prefixUrl));
            Long usedCount = tuple.get(Expressions.numberPath(Long.class, "usedCount"));
            responseDto.setCount(usedCount);
            responseDto.setRatio(totalCount != null && totalCount > 0 ? (double) usedCount / totalCount : 0);
            responseDtos.add(responseDto);
        }

        return responseDtos;
    }
}
