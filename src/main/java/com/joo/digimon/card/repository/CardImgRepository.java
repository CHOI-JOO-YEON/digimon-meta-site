package com.joo.digimon.card.repository;

import com.joo.digimon.card.model.CardEntity;
import com.joo.digimon.card.model.CardImgEntity;
import com.joo.digimon.crawling.model.CrawlingCardEntity;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardImgRepository extends JpaRepository<CardImgEntity, Integer>, QuerydslPredicateExecutor<CardImgEntity> {
    @EntityGraph("CardImgEntity.detail")
    List<CardImgEntity> findByUploadUrlIsNullAndIsEnCardIsNull();

    @EntityGraph("CardImgEntity.detail")
    List<CardImgEntity> findByUploadUrlIsNullAndIsEnCardTrue();

    @EntityGraph("CardImgEntity.detail")
    List<CardImgEntity> findByUploadUrlIsNull();

//    @EntityGraph("CardImgEntity.detail")
    List<CardImgEntity> findByCardEntity(CardEntity cardEntity);

    @EntityGraph("CardImgEntity.detail")
    List<CardImgEntity> findByIdIn(List<Integer> ids, Sort sort);

    List<CardImgEntity> findByIdIn(List<Integer> ids);

    Optional<CardImgEntity> findByCrawlingCardEntity(CrawlingCardEntity crawlingCardEntity);

    @Override
    @EntityGraph(value = "CardImgEntity.detail", type = EntityGraph.EntityGraphType.LOAD)
    Page<CardImgEntity> findAll(Predicate predicate, Pageable pageable);


    @EntityGraph(value = "CardImgEntity.detail")
    Optional<CardImgEntity> findByCardEntityCardNoAndIsParallelFalse(String cardNo);
}
