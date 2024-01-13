package com.joo.digimon.crawling.repository;

import com.joo.digimon.crawling.model.CrawlingCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrawlingCardRepository extends JpaRepository<CrawlingCardEntity,Integer> {
}
