package com.joo.digimon.card.repository;

import com.joo.digimon.card.model.EnglishCardEntity;
import com.joo.digimon.card.model.JapaneseCardEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JapaneseCardRepository extends JpaRepository<JapaneseCardEntity, Integer> {
    List<JapaneseCardEntity> findByUploadUrlIsNull();
    
    @EntityGraph("JapaneseCardEntity.detail")
    List<JapaneseCardEntity> findByWebpUrlIsNull(Pageable pageable);
    List<JapaneseCardEntity> findByWebpUrlIsNull();
    @EntityGraph("JapaneseCardEntity.detail")
    List<JapaneseCardEntity> findByOriginUrlIsNotNullAndUploadUrlIsNull();
}
