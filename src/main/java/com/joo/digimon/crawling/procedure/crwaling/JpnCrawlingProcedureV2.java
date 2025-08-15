package com.joo.digimon.crawling.procedure.crwaling;

import com.joo.digimon.global.enums.Locale;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class JpnCrawlingProcedureV2 implements CrawlingProcedure {

    private final Element element;

    public JpnCrawlingProcedureV2(Element element) {
        this.element = element;
    }

    @Override
    public String getCardNo() {
        return element.selectFirst(".cardTitleList .cardNo").text().trim();
    }

    @Override
    public String getRarity() {
        return element.selectFirst(".cardTitleList .cardRarity").text().trim();
    }

    @Override
    public String getCardType() {
        return element.selectFirst(".cardTitleList .cardType").text().trim();
    }

    @Override
    public String getLv() {
        Element lv = element.selectFirst(".cardTitleList .cardLv");
        return lv != null ? lv.text().trim() : null;
    }

    @Override
    public Boolean isParallel() {
        if (!element.select(".cardParallel").isEmpty()) return true;

        Element titleArea = element.selectFirst(".cardTitleCol, .cardTitleList, .cardTitle");
        if (titleArea != null) {
            String t = titleArea.text().replace("\u00A0", " ").trim();
            return t.contains("パラレル") || t.contains("Parallel");
        }
        return false;
    }

    @Override
    public String getCardName() {
        return element.selectFirst(".cardTitle").text().trim();
    }

    @Override
    public String getForm() {
        return textOfDt("形態");
    }

    @Override
    public String getAttribute() {
        return textOfDt("属性");
    }

    @Override
    public String getType() {
        return textOfDt("タイプ");
    }

    @Override
    public String getDP() {
        return textOfDt("DP");
    }

    @Override
    public String getPlayCost() {
        return textOfDt("登場コスト");
    }

    @Override
    public String getDigivolveCost1() {
        return textOfDt("進化コスト1");
    }

    @Override
    public String getDigivolveCost2() {
        return textOfDt("進化コスト2");
    }

    @Override
    public String getEffect() {
        return CrawlingProcedure.parseElementToPlainText(findTextSection("上段テキスト"));
    }

    @Override
    public String getSourceEffect() {
        return CrawlingProcedure.parseElementToPlainText(findTextSection("下段テキスト"));
    }

    @Override
    public String getNote() {
        return textOfDt("入手情報");
    }

    @Override
    public String getImgUrl() {
        Element img = element.selectFirst(".cardImgInner img");
        if (img == null) img = element.selectFirst(".card_img img");
        return img != null ? img.attr("src") : "";
    }

    @Override
    public Locale getLocale() {
        return Locale.JPN;
    }

    @Override
    public List<String> getColors() {
        List<String> colors = new ArrayList<>();
        for (Element span : element.select("dd.cardInfoData.cardColor span")) {
            String txt = span.text().trim();
            if (!txt.isEmpty()) colors.add(txt);
        }
        return colors;
    }

    /* =========================
       Helpers
       ========================= */

    private String textOfDt(String dtTitle) {
        Element dd = element.selectFirst(
                "dl.cardInfoBox:has(> dt.cardInfoTit:matchesOwn(^\\s*" + escape(dtTitle) + "\\s*$)) > dd.cardInfoData"
        );
        if (dd == null) {
            dd = element.selectFirst(
                    "dl.cardInfoBox:has(> dt:matchesOwn(^\\s*" + escape(dtTitle) + "\\s*$)) > dd"
            );
        }
        return dd != null ? dd.text().trim() : null;
    }

    private Elements findTextSection(String sectionTitle) {
        Element container = element.selectFirst(
                ".cardInfoBox:has(.cardInfoTitMedium:matchesOwn(" + escape(sectionTitle) + "))," +
                        ".cardInfoBox:has(.cardInfoTit:matchesOwn(" + escape(sectionTitle) + "))"
        );
        return container != null ? container.select("dd, dd.cardInfoData") : new Elements();
    }

    private static String escape(String s) {
        return s.replace("(", "\\(")
                .replace(")", "\\)")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace(".", "\\.")
                .replace("?", "\\?")
                .replace("+", "\\+")
                .replace("*", "\\*")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace("|", "\\|")
                .replace("^", "\\^")
                .replace("$", "\\$");
    }
}
