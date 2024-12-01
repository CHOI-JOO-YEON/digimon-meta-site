package com.joo.digimon.crawling.procedure;

import com.joo.digimon.crawling.dto.CrawlingCardDto;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KorCrawlingProcedure implements CrawlingProcedure {

    Element element;

    public KorCrawlingProcedure(Element element) {
        this.element = element;
    }

    @Override
    public CrawlingCardDto crawl() {
        CrawlingCardDto crawlingCardDto = new CrawlingCardDto();

        crawlingCardDto.setLocale("KOR");
        crawlingCardDto.setIsParallel(!element.select(".cardParallel").isEmpty());
        extractCardInfoHead(element, crawlingCardDto);
        extractCardColor(crawlingCardDto, element);
        extractCardInfoBottom(element, crawlingCardDto);
        extractCardInfoBody(element, crawlingCardDto);
        return crawlingCardDto;
    }

    private void extractCardColor(CrawlingCardDto crawlingCardDto, Element element) {
        String classAttribute = element.selectFirst(".card_detail").className();
        Pattern pattern = Pattern.compile("card_detail_(\\w+)");
        Matcher matcher = pattern.matcher(classAttribute);

        if (matcher.find()) {
            String[] colorText = matcher.group(1).split("_");
            crawlingCardDto.setColor1(colorText[0]);
            if (colorText.length > 1) {
                crawlingCardDto.setColor2(colorText[1]);
            }
            if (colorText.length > 2) {
                crawlingCardDto.setColor3(colorText[2]);
            }
        }
    }

    private void extractCardInfoHead(Element element, CrawlingCardDto crawlingCardDto) {
        crawlingCardDto.setCardNo(element.selectFirst(".cardno").text());
        crawlingCardDto.setRarity(element.select(".cardinfo_head>li").get(1).text());
        crawlingCardDto.setCardType(element.selectFirst(".cardtype").text());
    }

    private void extractCardInfoBody(Element element, CrawlingCardDto crawlingCardDto) {
        Element lvElement = element.selectFirst(".cardlv");
        if (lvElement != null) {
            crawlingCardDto.setLv(lvElement.text());
        }


        crawlingCardDto.setCardName(changeJapanMiddlePoint(element.selectFirst(".card_name").text()));
        crawlingCardDto.setForm(element.select(".cardinfo_top_body dl:contains(형태) dd").text());
        crawlingCardDto.setImgUrl(element.select(".card_img>img").attr("src"));
        crawlingCardDto.setAttribute(element.select(".cardinfo_top_body dl:contains(속성) dd").text());
        crawlingCardDto.setType(element.select(".cardinfo_top_body dl:contains(유형) dd").text());
        crawlingCardDto.setDP(element.select(".cardinfo_top_body dl:contains(DP) dd").text());
        crawlingCardDto.setPlayCost(element.select(".cardinfo_top_body dl:contains(등장 코스트) dd").text());
        crawlingCardDto.setDigivolveCost1(element.select(".cardinfo_top_body dl:contains(진화 코스트 1) dd").text());
        crawlingCardDto.setDigivolveCost2(element.select(".cardinfo_top_body dl:contains(진화 코스트 2) dd").text());
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

    private void extractCardInfoBottom(Element element, CrawlingCardDto crawlingCardDto) {
        crawlingCardDto.setEffect(changeJapanMiddlePoint(CrawlingProcedure.parseElementToPlainText(element.select(".cardinfo_bottom dl:contains(상단 텍스트) dd"))));
        crawlingCardDto.setSourceEffect(changeJapanMiddlePoint(CrawlingProcedure.parseElementToPlainText(element.select(".cardinfo_bottom dl:contains(하단 텍스트) dd"))));
        crawlingCardDto.setNote(changeJapanMiddlePoint(element.select(".cardinfo_bottom dl:contains(입수 정보) dd").text()));
    }
}
