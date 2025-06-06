package com.joo.digimon.card.repository;

import com.joo.digimon.card.model.CardEntity;
import com.joo.digimon.global.enums.CardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CardRepository extends JpaRepository<CardEntity,Integer>{
    Optional<CardEntity> findByCardNo(String cardNo);

    List<CardEntity> findByCardTypeAndIsOnlyEnCardIsNullOrIsOnlyEnCardIsFalse(CardType cardType);

    List<CardEntity> findByCardNoIn(Collection<String> cardNos);
}
