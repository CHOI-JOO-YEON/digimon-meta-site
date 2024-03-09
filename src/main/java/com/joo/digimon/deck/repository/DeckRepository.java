package com.joo.digimon.deck.repository;

import com.joo.digimon.deck.model.DeckEntity;
import com.joo.digimon.user.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeckRepository extends JpaRepository<DeckEntity,Integer> {
    @Override
    @EntityGraph("Deck.detail")
    Optional<DeckEntity> findById(Integer id);

    @EntityGraph("Deck.detail")
    List<DeckEntity> findByUserOrderByCreatedDateTime(User user);

    List<DeckEntity> findByIsPublicIsTrue();
}
