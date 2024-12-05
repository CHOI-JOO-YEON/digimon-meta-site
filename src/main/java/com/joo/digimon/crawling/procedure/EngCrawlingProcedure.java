package com.joo.digimon.crawling.procedure;

import com.joo.digimon.global.enums.Locale;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EngCrawlingProcedure implements CrawlingProcedure {

    Element element;

    public EngCrawlingProcedure(Element element) {
        this.element = element;
    }

    @Override
    public String getCardNo() {
        return Objects.requireNonNull(element.selectFirst(".cardno")).text();
    }

    @Override
    public String getRarity() {
        return element.select(".cardinfo_head>li").get(1).text();
    }

    @Override
    public String getCardType() {
        return Objects.requireNonNull(element.selectFirst(".cardtype")).text();
    }

    @Override
    public String getLv() {
        Element lvElement = element.selectFirst(".cardlv");
        if (lvElement != null) {
            return lvElement.text();
        }
        return null;
    }

    @Override
    public Boolean isParallel() {
        return !element.select(".cardParallel").isEmpty();
    }

    @Override
    public String getCardName() {
        return Objects.requireNonNull(element.selectFirst(".card_name")).text();
    }

    @Override
    public String getForm() {
        return element.select(".cardinfo_top_body dl:contains(Form) dd").text();
    }

    @Override
    public String getAttribute() {
        return element.select(".cardinfo_top_body dl:contains(Attribute) dd").text();
    }

    @Override
    public String getType() {
        return element.select(".cardinfo_top_body dl:contains(Type) dd").text();
    }

    @Override
    public String getDP() {
        return element.select(".cardinfo_top_body dl:contains(DP) dd").text();
    }

    @Override
    public String getPlayCost() {
        return element.select(".cardinfo_top_body dl:contains(Play Cost) dd").text();
    }

    @Override
    public String getDigivolveCost1() {
        return element.select(".cardinfo_top_body dl:contains(Digivolve Cost 1) dd").text();
    }

    @Override
    public String getDigivolveCost2() {
        return element.select(".cardinfo_top_body dl:contains(Digivolve Cost 2) dd").text();
    }

    @Override
    public String getEffect() {
        return CrawlingProcedure.parseElementToPlainText(element.select(".cardinfo_bottom dl dt:matchesOwn(^Effect$) + dd"));
    }

    @Override
    public String getSourceEffect() {
        return CrawlingProcedure.parseElementToPlainText(element.select(".cardinfo_bottom dl:contains(Inherited Effect) dd"));
    }

    @Override
    public String getNote() {
        return element.select(".cardinfo_bottom dl:contains(Notes) dd").text();
    }

    @Override
    public String getImgUrl() {
        return element.select(".card_img>img").attr("src");
    }

    @Override
    public Locale getLocale() {
        return Locale.ENG;
    }

    @Override
    public List<String> getColors() {
        List<String> colors = new ArrayList<>();
        Elements colorSpans = element.select("dd.cardColor span");

        for (Element span : colorSpans) {
            String color = span.text();
            colors.add(color);
        }
        return colors;
    }
}
