package com.joo.digimon.crawling.service;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.List;


@ActiveProfiles("dev")
@SpringBootTest
class CrawlingServiceImplTest {
    @Autowired
    CrawlingServiceImpl crawlingService;

    @Test
    void urlToDocumentListTest() throws IOException {
        List<Document> documentListByFirstPageUrl = crawlingService.getDocumentListByFirstPageUrl("https://digimoncard.co.kr/index.php?mid=cardlist&category=1078");
        Assertions.assertEquals(11,documentListByFirstPageUrl.size());
    }


}