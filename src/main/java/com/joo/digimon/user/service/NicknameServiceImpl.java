package com.joo.digimon.user.service;

import com.joo.digimon.card.model.CardEntity;
import com.joo.digimon.card.model.QCardEntity;
import com.joo.digimon.card.repository.CardRepository;
import com.joo.digimon.global.enums.CardType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NicknameServiceImpl implements NicknameService {
    private final CardRepository cardRepository;
    private final EntityManager entityManager;


    @Override
    public String generateNickname() {
        List<CardEntity> allEntities =findByCardTypeAndIsOnlyEnCardIsNullOrIsFalse(CardType.DIGIMON);
        if (allEntities.isEmpty()) {
            return "이그드라실";
        }
        int randomIndex = (int) (Math.random() * allEntities.size());
        return allEntities.get(randomIndex).getCardName();



    }
    @Override
    public List<CardEntity> findByCardTypeAndIsOnlyEnCardIsNullOrIsFalse(CardType cardType) {
        QCardEntity card = QCardEntity.cardEntity;

        return new JPAQueryFactory(entityManager)
                .selectFrom(card)
                .where(card.cardType.eq(cardType)
                        .and(card.isOnlyEnCard.isNull().or(card.isOnlyEnCard.isFalse())))
                .fetch();
    }
}
