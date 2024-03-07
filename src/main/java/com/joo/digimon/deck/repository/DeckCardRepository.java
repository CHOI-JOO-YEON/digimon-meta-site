package com.joo.digimon.deck.repository;

import com.joo.digimon.deck.model.DeckCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeckCardRepository extends JpaRepository<DeckCardEntity, Integer> {
}
