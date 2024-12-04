package com.joo.digimon.card.repository;

import com.joo.digimon.card.model.JapaneseCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JapaneseCardRepository extends JpaRepository<JapaneseCardEntity, Integer> {

}
