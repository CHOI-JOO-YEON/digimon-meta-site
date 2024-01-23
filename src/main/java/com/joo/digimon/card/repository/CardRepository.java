package com.joo.digimon.card.repository;

import com.joo.digimon.card.model.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<CardEntity,Integer>{
    Optional<CardEntity> findByCardNo(String cardNo);
}
