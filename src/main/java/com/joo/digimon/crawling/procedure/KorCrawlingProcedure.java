package com.joo.digimon.crawling.procedure;

import com.joo.digimon.global.enums.Locale;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KorCrawlingProcedure implements CrawlingProcedure {

    Element element;

    public KorCrawlingProcedure(Element element) {
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
        return changeJapanMiddlePoint(Objects.requireNonNull(element.selectFirst(".card_name")).text());
    }

    @Override
    public String getForm() {
        return element.select(".cardinfo_top_body dl:contains(형태) dd").text();
    }

    @Override
    public String getAttribute() {
        return element.select(".cardinfo_top_body dl:contains(속성) dd").text();
    }

    @Override
    public String getType() {
        return element.select(".cardinfo_top_body dl:contains(유형) dd").text();
    }

    @Override
    public String getDP() {
        return element.select(".cardinfo_top_body dl:contains(DP) dd").text();
    }

    @Override
    public String getPlayCost() {
        return element.select(".cardinfo_top_body dl:contains(등장 코스트) dd").text();
    }

    @Override
    public String getDigivolveCost1() {
        return element.select(".cardinfo_top_body dl:contains(진화 코스트 1) dd").text();
    }

    @Override
    public String getDigivolveCost2() {
        return element.select(".cardinfo_top_body dl:contains(진화 코스트 2) dd").text();
    }

    @Override
    public String getEffect() {
        return changeJapanMiddlePoint(CrawlingProcedure.parseElementToPlainText(element.select(".cardinfo_bottom dl:contains(상단 텍스트) dd")));
    }

    @Override
    public String getSourceEffect() {
        return changeJapanMiddlePoint(CrawlingProcedure.parseElementToPlainText(element.select(".cardinfo_bottom dl:contains(하단 텍스트) dd")));
    }

    @Override
    public String getNote() {
        return changeJapanMiddlePoint(element.select(".cardinfo_bottom dl:contains(입수 정보) dd").text());
    }

    @Override
    public String getImgUrl() {
        return element.select(".card_img>img").attr("src");
    }

    @Override
    public Locale getLocale() {
        return Locale.KOR;
    }

    @Override
    public List<String> getColors() {
        String classAttribute = Objects.requireNonNull(element.selectFirst(".card_detail")).className();
        Pattern pattern = Pattern.compile("card_detail_(\\w+)");
        Matcher matcher = pattern.matcher(classAttribute);

        List<String> colors = new ArrayList<>();
        if (matcher.find()) {
            String[] colorTexts = matcher.group(1).split("_");
            colors.add(colorTexts[0]);
            if (colorTexts.length > 1) {
                colors.add(colorTexts[1]);
            }
            if (colorTexts.length > 2) {
                colors.add(colorTexts[2]);
            }
        }

        return colors;
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
