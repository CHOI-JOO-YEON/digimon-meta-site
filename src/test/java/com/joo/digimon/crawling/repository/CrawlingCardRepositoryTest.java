package com.joo.digimon.crawling.repository;


import com.joo.digimon.card.model.CardImgEntity;
import com.joo.digimon.card.model.ParallelCardImgEntity;
import com.joo.digimon.card.repository.CardImgRepository;
import com.joo.digimon.card.repository.ParallelCardImgRepository;
import com.joo.digimon.crawling.model.CrawlingCardEntity;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
class CrawlingCardRepositoryTest {
    @Autowired
    CardImgRepository cardImgRepository;

    @Autowired
    ParallelCardImgRepository parallelCardImgRepository;
    @Autowired
    CrawlingCardRepository crawlingCardRepository;

    @Test
    @Transactional
    void findByCardImgEntityIsNullAndParallelCardImgEntityIsNullSuccessTest() {
        CrawlingCardEntity cr1 = crawlingCardRepository.save(CrawlingCardEntity.builder()
                .cardNo("1")
                .build());
        CrawlingCardEntity cr2 = crawlingCardRepository.save(CrawlingCardEntity.builder()
                .cardNo("2")
                .build());
        crawlingCardRepository.save(CrawlingCardEntity.builder()
                .cardNo("3")
                .build());
        cardImgRepository.save(CardImgEntity.builder()
                .crawlingCardEntity(cr1)
                .build());
        parallelCardImgRepository.save(ParallelCardImgEntity.builder()
                .crawlingCardEntity(cr2)
                .build());

        Assertions.assertEquals(3, crawlingCardRepository.findAll().size());
        Assertions.assertEquals(1, crawlingCardRepository.findByCardImgEntityIsNullAndParallelCardImgEntityIsNull().size());
    }

}