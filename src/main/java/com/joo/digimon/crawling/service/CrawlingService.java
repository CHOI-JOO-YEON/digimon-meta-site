package com.joo.digimon.crawling.service;

import com.joo.digimon.crawling.dto.CrawlingCardDto;
import com.joo.digimon.crawling.dto.ReflectCardRequestDto;
import com.joo.digimon.crawling.dto.ReflectCardResponseDto;
import com.joo.digimon.crawling.model.CrawlingCardEntity;
import jakarta.transaction.Transactional;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;

public interface CrawlingService {

    List<CrawlingCardDto> getUnreflectedCrawlingCardDtoList(Integer size);

    @Transactional
    List<ReflectCardResponseDto> saveCardByReflectCardRequestList(List<ReflectCardRequestDto> reflectCardRequestDtoList);

    int crawlAndSaveByUrl(String url) throws IOException;

    List<CrawlingCardEntity> crawlUrlAndBuildEntityList(String url) throws IOException;
    List<Document> getDocumentListByFirstPageUrl(String url) throws IOException;
    List<Element> getCardElementsByDocument(Document document);
    CrawlingCardDto crawlingCardByElement(Element element);
}
