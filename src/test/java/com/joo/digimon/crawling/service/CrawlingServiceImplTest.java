package com.joo.digimon.crawling.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
    void urlToDocumentListSuccessTest() throws IOException {
        List<Document> documentListByFirstPageUrl = crawlingService.getDocumentListByFirstPageUrl("https://digimoncard.co.kr/index.php?mid=cardlist&category=1078");
        Assertions.assertEquals(11,documentListByFirstPageUrl.size());
    }

    @Test
    void urlToDocumentListFailTest() throws IOException {
        Assertions.assertThrows(IllegalArgumentException.class,() ->
                crawlingService.getDocumentListByFirstPageUrl("https://www.naver.com/"));
    }

    @Test
    void documentToElementListSuccessTest() throws IOException {
        List<Document> documentListByFirstPageUrl = crawlingService.getDocumentListByFirstPageUrl("https://digimoncard.co.kr/index.php?mid=cardlist&category=1078");
        List<Element> elements = crawlingService.getCardElementsByDocument(documentListByFirstPageUrl.get(0));
        Assertions.assertEquals(20,elements.size());

    }

    @Test
    void documentToElementListFailTest() throws IOException {
        Document document = Jsoup.connect("https://www.naver.com/").get();
        List<Element> elements = crawlingService.getCardElementsByDocument(document);
        Assertions.assertEquals(0,elements.size());
    }

}