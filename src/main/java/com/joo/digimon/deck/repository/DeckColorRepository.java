package com.joo.digimon.deck.repository;

import com.joo.digimon.deck.model.DeckColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeckColorRepository extends JpaRepository<DeckColor,Long> {
}
