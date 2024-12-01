package com.joo.digimon.crawling.procedure;

import com.joo.digimon.crawling.dto.CrawlingCardDto;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class EngCrawlingProcedure implements CrawlingProcedure {

    Element element;

    public EngCrawlingProcedure(Element element) {
        this.element = element;
    }

    @Override
    public CrawlingCardDto crawl() {
        CrawlingCardDto crawlingCardDto = new CrawlingCardDto();

        crawlingCardDto.setLocale("ENG");
        crawlingCardDto.setIsParallel(!element.select(".cardParallel").isEmpty());
        crawlingCardDto.setForm(element.select(".cardinfo_top_body dl:contains(Form) dd").text());

        extractCardInfoHead(element, crawlingCardDto);
        extractEngCardInfoBottom(element, crawlingCardDto);
        extractEngCardInfoTop(element, crawlingCardDto);
        return crawlingCardDto;
    }

    private static void extractCardInfoHead(Element element, CrawlingCardDto crawlingCardDto) {
        crawlingCardDto.setCardNo(element.selectFirst(".cardno").text());
        crawlingCardDto.setRarity(element.select(".cardinfo_head>li").get(1).text());
        crawlingCardDto.setCardType(element.selectFirst(".cardtype").text());
    }
    private static void extractEngCardInfoBottom(Element element, CrawlingCardDto crawlingCardDto) {
        crawlingCardDto.setEffect(CrawlingProcedure.parseElementToPlainText(element.select(".cardinfo_bottom dl dt:matchesOwn(^Effect$) + dd")));
        crawlingCardDto.setSourceEffect(CrawlingProcedure.parseElementToPlainText(element.select(".cardinfo_bottom dl:contains(Inherited Effect) dd")));
        crawlingCardDto.setNote(element.select(".cardinfo_bottom dl:contains(Notes) dd").text());
    }

    private static void extractEngCardInfoTop(Element element, CrawlingCardDto crawlingCardDto) {
        Element lvElement = element.selectFirst(".cardlv");
        if (lvElement != null) {
            crawlingCardDto.setLv(lvElement.text());
        }
        extractColor(element, crawlingCardDto);

        crawlingCardDto.setCardName(element.selectFirst(".card_name").text());
        crawlingCardDto.setImgUrl(element.select(".card_img>img").attr("src"));
        crawlingCardDto.setForm(element.select(".cardinfo_top_body dl:contains(Form) dd").text());
        crawlingCardDto.setAttribute(element.select(".cardinfo_top_body dl:contains(Attribute) dd").text());
        crawlingCardDto.setType(element.select(".cardinfo_top_body dl:contains(Type) dd").text());
        crawlingCardDto.setDP(element.select(".cardinfo_top_body dl:contains(DP) dd").text());
        crawlingCardDto.setPlayCost(element.select(".cardinfo_top_body dl:contains(Play Cost) dd").text());
        crawlingCardDto.setDigivolveCost1(element.select(".cardinfo_top_body dl:contains(Digivolve Cost 1) dd").text());
        crawlingCardDto.setDigivolveCost2(element.select(".cardinfo_top_body dl:contains(Digivolve Cost 2) dd").text());
    }

    private static void extractColor(Element element, CrawlingCardDto crawlingCardDto) {
        Elements colorSpans = element.select("dd.cardColor span");
        int index = 0;
        for (Element span : colorSpans) {
            String color = span.text();
            if (index == 0) {
                crawlingCardDto.setColor1(color);
            } else if (index == 1) {
                crawlingCardDto.setColor2(color);
            } else if (index == 2) {
                crawlingCardDto.setColor3(color);
            }
            index++;
        }
    }

}
