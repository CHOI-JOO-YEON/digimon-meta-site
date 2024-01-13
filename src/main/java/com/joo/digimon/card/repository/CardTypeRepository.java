package com.joo.digimon.card.repository;

import com.joo.digimon.card.model.CardCombineTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardTypeRepository extends JpaRepository<CardCombineTypeEntity, Integer> {
}
