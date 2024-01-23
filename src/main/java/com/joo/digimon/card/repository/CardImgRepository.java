package com.joo.digimon.card.repository;

import com.joo.digimon.card.model.CardEntity;
import com.joo.digimon.card.model.CardImgEntity;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardImgRepository extends JpaRepository<CardImgEntity,Integer>, QuerydslPredicateExecutor<CardImgEntity> {
    @EntityGraph("CardImgEntity.detail")
    List<CardImgEntity> findByUploadUrlIsNull();

    @EntityGraph("CardImgEntity.detail")
    Optional<CardImgEntity> findByCardEntity(CardEntity cardEntity);

    @Override
    @EntityGraph(value = "CardImgEntity.detail", type = EntityGraph.EntityGraphType.LOAD)
    Page<CardImgEntity> findAll(Predicate predicate, Pageable pageable);
}
