package com.joo.digimon.card.repository;

import com.joo.digimon.card.model.EnglishCardEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnglishCardRepository extends JpaRepository<EnglishCardEntity, Integer> {
    List<EnglishCardEntity> findByUploadUrlIsNull();
    
    @EntityGraph("EnglishCardEntity.detail")
    List<EnglishCardEntity> findByOriginUrlIsNotNullAndUploadUrlIsNull();

    @EntityGraph("EnglishCardEntity.detail")
    List<EnglishCardEntity> findByWebpUrlIsNullAndOriginUrlIsNotNull(Pageable pageable);
    List<EnglishCardEntity> findByWebpUrlIsNullAndOriginUrlIsNotNull();
}
