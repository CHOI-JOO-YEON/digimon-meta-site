package com.joo.digimon.deck.repository;

import com.joo.digimon.card.model.CardImgEntity;
import com.joo.digimon.deck.model.DeckEntity;
import com.joo.digimon.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import com.querydsl.core.types.Predicate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeckRepository extends JpaRepository<DeckEntity,Integer> , QuerydslPredicateExecutor<DeckEntity> {
    @Override
    @EntityGraph("Deck.detail")
    Optional<DeckEntity> findById(Integer id);

    @Override
    @EntityGraph("Deck.detail")
    Page<DeckEntity> findAll(Predicate predicate, Pageable pageable);

    @EntityGraph("Deck.detail")
    List<DeckEntity> findByUser(User user);

    @EntityGraph("Deck.detail")
    List<DeckEntity> findByUserOrderByCreatedDateTime(User user);

    List<DeckEntity> findByIsPublicIsTrue();
}
