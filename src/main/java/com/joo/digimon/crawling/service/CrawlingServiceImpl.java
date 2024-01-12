package com.joo.digimon.crawling.service;

import com.joo.digimon.crawling.dto.CrawlingCardDto;
import com.joo.digimon.crawling.model.CrawlingCardEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CrawlingServiceImpl implements CrawlingService{
    @Override
    public List<CrawlingCardEntity> crawlUrlAndBuildEntityList(String url) {
        return null;
    }

    @Override
    public List<Document> getDocumentListByFirstPageUrl(String url) throws IOException {
        Document doc= Jsoup.connect(url).get();
        List<Document> documentList = new ArrayList<>();
        documentList.add(doc);
        Elements pages = doc.selectFirst(".paging").select("ul>li>a");
        for (Element page : pages) {
            if (page.text().equals("Next"))
                break;
            documentList.add(Jsoup.connect(url).get());
        }
        return documentList;
    }

    @Override
    public List<Element> getCardElementsByDocument(Document document) {
        return null;
    }

    @Override
    public CrawlingCardDto crawlingCardByElement(Element element) {
        return null;
    }
}
