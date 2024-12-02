package com.joo.digimon.crawling.service;

import com.joo.digimon.crawling.dto.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;

public interface CrawlingService {

    CrawlingResultDto crawlAndSaveByUrl(String url, String locale, String note) throws IOException;

    List<Document> getDocumentListByFirstPageUrl(String url) throws IOException;

    List<Element> getCardElementsByDocument(Document document);

    CrawlingCardDto crawlingCardByElement(Element element, String locale);

}
