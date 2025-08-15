package com.joo.digimon.crawling.procedure.parse;

import com.joo.digimon.crawling.model.CrawlingCardEntity;
import com.joo.digimon.global.enums.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JpnCardParseProcedure implements CardParseProcedure {

    CrawlingCardEntity card;

    public JpnCardParseProcedure(CrawlingCardEntity card) {
        this.card = card;
    }

    @Override
    public Integer getId() {
        return card.getId();
    }

    @Override
    public String getCardNo() {
        return card.getCardNo();
    }

    @Override
    public Rarity getRarity() {
        return Rarity.parseRarity(card.getRarity());
    }

    @Override
    public CardType getCardType() {
        return CardType.findByString(card.getCardType(), card.getLocale());
    }

    @Override
    public Integer getLv() {
        CardType cardType = getCardType();
        if (cardType.equals(CardType.OPTION) || cardType.equals(CardType.TAMER)) {
            return null;
        }
        if (card.getLv() == null) {
            return -1;
        }
        String replace = card.getLv().replace("Lv.", "");
        if (replace.equals("-")) {
            return 0;
        }
        return Integer.parseInt(replace);
    }

    @Override
    public Boolean isParallel() {
        return card.getIsParallel();
    }

    @Override
    public String getCardName() {
        return card.getCardName();
    }

    @Override
    public Form getForm() {
        CardType cardType = getCardType();
        if (cardType.equals(CardType.OPTION) || cardType.equals(CardType.TAMER)) {
            return null;
        }
        return Form.findForm(card.getForm(), card.getLocale());
    }

    @Override
    public Attribute getAttribute() {
        if (card.getAttribute() == null) {
            return null;
        }
        if (card.getAttribute().equals("-")) {
            return null;
        }
        return Attribute.findByString(card.getAttribute(), card.getLocale());
    }

    @Override
    public List<String> getTypes() {
        if (card.getType() == null || card.getType().isEmpty()) {
            return new ArrayList<>();
        }
        String typeString = card.getType().replace("-", "");
        String[] types = typeString.split("/");
        List<String> typeList = new ArrayList<>();
        for (String s : types) {
            if(!s.isEmpty()) {
                typeList.add(s.trim());
            }
        }
        return typeList;
    }

    @Override
    public Integer getDp() {
        if (card.getDP() == null) {
            return null;
        }
        if (card.getDP().equals("-")) {
            return null;
        }
        try {
            double doubleValue = Double.parseDouble(card.getDP());
            return (int) doubleValue;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    public Integer getPlayCost() {
        if (card.getPlayCost() == null) {
            return null;
        }
        if (card.getPlayCost().equals("-")) {
            return null;
        }
        try {
            return Integer.parseInt(card.getPlayCost());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    public String getEffect() {
        if (card.getEffect() == null) {
            return null;
        }
        if (card.getEffect().equals("-")) {
            return null;
        }
        String effect = card.getEffect().replace("<img src=\"../images/cardlist/evolution.png\">", "〔進化〕");
        effect = effect.replace("<br>", "");
        String digicrossRegex = "<img src=\"\\.\\./images/cardlist/digicross-(\\d+)\\.png\">";
        effect = effect.replaceAll(digicrossRegex, "デジクロス-$1:");
        return effect;
    }

    @Override
    public String getSourceEffect() {
        if (card.getSourceEffect() == null) {
            return null;
        }
        if (card.getSourceEffect().equals("-")) {
            return null;
        }

        String regex = "<img src=\"\\.\\./images/cardlist/overflow-(\\d+)\\.png\">";
        return card.getSourceEffect().replaceAll(regex, "≪オーバーフロー《-$1》≫");
    }

    @Override
    public String getNote() {
        return card.getNote().replace("▹", "");
    }

    @Override
    public Color getColor1() {
        return Color.getColorByJpn(card.getColor1());
    }

    @Override
    public Color getColor2() { return Color.getColorByJpn(card.getColor2()); }

    @Override
    public Color getColor3() {
        return Color.getColorByJpn(card.getColor3());
    }

    @Override
    public String getOriginUrl() {
        return card.getImgUrl().substring(2);
    }

    @Override
    public Locale getLocale() {
        return Locale.JPN;
    }

    @Override
    public Digivolve getDigivolve1() {
        return getDigivolve(card.getDigivolveCost1());
    }

    @Override
    public Digivolve getDigivolve2() {
        return getDigivolve(card.getDigivolveCost2());
    }

    @Override
    public CrawlingCardEntity getCrawlingCardEntity() {
        return card;
    }

    private Digivolve getDigivolve(String digivolve) {
        if (digivolve == null || digivolve.equals("-")) {
            return new Digivolve(null, null);
        }
        String[] digivolveStrings = digivolve.split("から");
        if (digivolveStrings.length < 2) {
            return new Digivolve(null, null);
        }
        try {
            int digivolveCondition = Integer.parseInt(digivolveStrings[0].replace("Lv.", ""));
            double digivolveDoubleCost = Double.parseDouble(digivolveStrings[1]);
            int digivolveCost = (int) digivolveDoubleCost;
            return new Digivolve(digivolveCost, digivolveCondition);
        } catch (NumberFormatException e) {
            return new Digivolve(null, null);
        }
    }
}
