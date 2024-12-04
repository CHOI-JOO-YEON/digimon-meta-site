package com.joo.digimon.crawling.repository;

import com.joo.digimon.crawling.model.CrawlingCardEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CrawlingCardRepository extends JpaRepository<CrawlingCardEntity,Integer> {
    List<CrawlingCardEntity> findByCardImgEntityIsNullAndParallelCardImgEntityIsNull(Pageable pageable);

    Optional<CrawlingCardEntity> findByImgUrl(String url);

    Optional<CrawlingCardEntity> findByImgUrlAndLocale(String imgUrl, String locale);

    List<CrawlingCardEntity> findByLocale(String locale);
}
