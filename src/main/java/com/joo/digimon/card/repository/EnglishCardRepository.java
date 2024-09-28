package com.joo.digimon.card.repository;

import com.joo.digimon.card.model.EnglishCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnglishCardRepository extends JpaRepository<EnglishCardEntity, Integer> {

}
