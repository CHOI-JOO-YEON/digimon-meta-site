package com.joo.digimon.crawling.service;

import com.joo.digimon.crawling.dto.CrawlingCardDto;
import com.joo.digimon.crawling.model.CrawlingCardEntity;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;

public interface CrawlingService {

    List<CrawlingCardEntity> crawlUrlAndBuildEntityList(String url);
    List<Document> getDocumentListByFirstPageUrl(String url);
    List<Element> getCardElementsByDocument(Document document);
    CrawlingCardDto crawlingCardByElement(Element element);
}
