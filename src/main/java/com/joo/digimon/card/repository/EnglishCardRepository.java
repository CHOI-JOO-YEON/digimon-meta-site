package com.joo.digimon.card.repository;

import com.joo.digimon.card.model.EnglishCardEntity;
import com.joo.digimon.card.model.JapaneseCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnglishCardRepository extends JpaRepository<EnglishCardEntity, Integer> {
    List<EnglishCardEntity> findByUploadUrlIsNull();
    List<EnglishCardEntity> findByOriginUrlIsNotNullAndUploadUrlIsNull();
}
