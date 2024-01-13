package com.joo.digimon.crawling.service;

import com.joo.digimon.crawling.dto.CrawlingCardDto;
import com.joo.digimon.crawling.model.CrawlingCardEntity;
import com.joo.digimon.crawling.repository.CrawlingCardRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CrawlingServiceImpl implements CrawlingService {

    private final CrawlingCardRepository crawlingCardRepository;

    @Override
    public int crawlAndSaveByUrl(String url) throws IOException {
        List<CrawlingCardEntity> crawlingCardEntities = crawlUrlAndBuildEntityList(url);
        return crawlingCardRepository.saveAll(crawlingCardEntities).size();

    }
    @Override
    public List<CrawlingCardEntity> crawlUrlAndBuildEntityList(String url) throws IOException {
        List<Document> documentListByFirstPageUrl = getDocumentListByFirstPageUrl(url);

        List<Element> cardElement = new ArrayList<>();
        for (Document document : documentListByFirstPageUrl) {
            cardElement.addAll(getCardElementsByDocument(document));
        }
        List<CrawlingCardDto> crawlingCardDtoList = new ArrayList<>();
        for (Element element : cardElement) {
            crawlingCardDtoList.add(crawlingCardByElement(element));
        }
        List<CrawlingCardEntity> crawlingCardEntities = new ArrayList<>();

        for (CrawlingCardDto crawlingCardDto : crawlingCardDtoList) {
            crawlingCardEntities.add(
                    new CrawlingCardEntity(crawlingCardDto)
            );
        }


        return crawlingCardEntities;
    }

    @Override
    public List<Document> getDocumentListByFirstPageUrl(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        List<Document> documentList = new ArrayList<>();
        documentList.add(doc);
        try {
            Elements pages = doc.selectFirst(".paging").select("ul>li>a");
            for (Element page : pages) {
                if (page.text().equals("Next"))
                    break;
                documentList.add(Jsoup.connect(url).get());
            }
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("paging 클래스 태그를 찾을 수 없음");
        }


        return documentList;
    }

    @Override
    public List<Element> getCardElementsByDocument(Document document) {
        return document.select(".cardlistCol .popup");
    }

    @Override
    public CrawlingCardDto crawlingCardByElement(Element element) {
        CrawlingCardDto crawlingCardDto = new CrawlingCardDto();

        crawlingCardDto.setIsParallel(!element.select(".cardtype cardParallel").isEmpty());
        extractCardColor(crawlingCardDto, element);
        extractCardInfoHead(element, crawlingCardDto);
        extractCardInfoBody(element, crawlingCardDto);
        extractCardInfoBottom(element, crawlingCardDto);

        return crawlingCardDto;
    }

    private void extractCardInfoBottom(Element element, CrawlingCardDto crawlingCardDto) {
        crawlingCardDto.setEffect(changeJapanMiddlePoint(element.select(".cardinfo_bottom dl:contains(상단 텍스트) dd").text()));
        crawlingCardDto.setSourceEffect(changeJapanMiddlePoint(element.select(".cardinfo_bottom dl:contains(하단 텍스트) dd").text()));
        crawlingCardDto.setNote(changeJapanMiddlePoint(element.select(".cardinfo_bottom dl:contains(입수 정보) dd").text()));
    }

    private void extractCardInfoBody(Element element, CrawlingCardDto crawlingCardDto) {
        Element lvElement = element.selectFirst(".cardlv");
        if (lvElement != null) {
            crawlingCardDto.setLv(lvElement.text());
        }


        crawlingCardDto.setCardName(changeJapanMiddlePoint(element.selectFirst(".card_name").text()));

        crawlingCardDto.setImgUrl(element.select(".card_img>img").attr("src"));
        crawlingCardDto.setForm(element.select(".cardinfo_top_body dl:contains(형태) dd").text());
        crawlingCardDto.setAttribute(element.select(".cardinfo_top_body dl:contains(속성) dd").text());
        crawlingCardDto.setType(element.select(".cardinfo_top_body dl:contains(유형) dd").text());
        crawlingCardDto.setDP(element.select(".cardinfo_top_body dl:contains(DP) dd").text());
        crawlingCardDto.setPlayCost(element.select(".cardinfo_top_body dl:contains(등장 코스트) dd").text());
        crawlingCardDto.setDigivolveCost1(element.select(".cardinfo_top_body dl:contains(진화 코스트 1) dd").text());
        crawlingCardDto.setDigivolveCost2(element.select(".cardinfo_top_body dl:contains(진화 코스트 2) dd").text());
    }

    private void extractCardInfoHead(Element element, CrawlingCardDto crawlingCardDto) {
        crawlingCardDto.setCardNo(element.selectFirst(".cardno").text());
        crawlingCardDto.setRarity(element.select(".cardinfo_head>li").get(1).text());
        crawlingCardDto.setCardType(element.selectFirst(".cardtype").text());
    }

    private void extractCardColor(CrawlingCardDto crawlingCardDto, Element element) {
        String classAttribute = element.selectFirst(".card_detail").className();
        Pattern pattern = Pattern.compile("card_detail_(\\w+)");
        Matcher matcher = pattern.matcher(classAttribute);

        if (matcher.find()) {
            String[] colorText = matcher.group(1).split("_");
            crawlingCardDto.setColor1(colorText[0]);
            if (colorText.length == 2) {
                crawlingCardDto.setColor2(colorText[1]);
            }
        }
    }

    private String changeJapanMiddlePoint(String text) {
        char[] textArray = text.toCharArray();
        for (int i = 0; i < textArray.length; i++) {
            if (textArray[i] == 12539) {
                textArray[i] = '·';
            }
        }
        return new String(textArray);
    }
}
