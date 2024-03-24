package com.joo.digimon.crawling.service;

import com.joo.digimon.crawling.dto.ReflectCardRequestDto;
import com.joo.digimon.crawling.enums.CardType;
import com.joo.digimon.crawling.enums.Color;
import com.joo.digimon.crawling.enums.Form;
import com.joo.digimon.crawling.enums.Rarity;
import com.joo.digimon.crawling.model.CrawlingCardEntity;
import com.joo.digimon.exception.message.CardParseExceptionMessage;
import com.joo.digimon.exception.model.CardParseException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CardParseServiceImpl implements CardParseService {
    @Override
    public ReflectCardRequestDto crawlingCardParse(CrawlingCardEntity crawlingCard) throws CardParseException {
        ReflectCardRequestDto dto = new ReflectCardRequestDto();
        dto.setId(crawlingCard.getId());
        dto.setCardNo(parseCardNo(crawlingCard.getCardNo()));
        dto.setRarity(parseCardRarity(crawlingCard.getRarity()));
        dto.setCardType(parseCardType(crawlingCard.getCardType()));
        dto.setLv(parseLv(crawlingCard.getLv(), dto.getCardType()));
        dto.setIsParallel(crawlingCard.getIsParallel());
        dto.setCardName(parseCardName(crawlingCard.getCardName()));
        dto.setForm(parseForm(crawlingCard.getForm(), dto.getCardType()));
        dto.setAttribute(parseAttribute(crawlingCard.getAttribute()));
        dto.setTypes(parseTypes(crawlingCard.getType(), dto.getCardType()));
        dto.setDp(parseDp(crawlingCard.getDP()));
        dto.setPlayCost(parsePlayCost(crawlingCard.getPlayCost()));
        dto.setEffect(parseEffect(crawlingCard.getEffect()));
        dto.setSourceEffect(parseSourceEffect(crawlingCard.getSourceEffect()));
        dto.setNote(parseNote(crawlingCard.getNote()));

        dto.setColor1(parseColor(crawlingCard.getColor1()));
        dto.setColor2(parseColor(crawlingCard.getColor2()));
        dto.setOriginUrl(parseUrl(crawlingCard.getImgUrl()));

        setDtoDigivolve(dto, crawlingCard);
        return dto;
    }

    @Override
    public ReflectCardRequestDto crawlingCardParseEn(CrawlingCardEntity crawlingCard) throws CardParseException {
        ReflectCardRequestDto dto = new ReflectCardRequestDto();
        dto.setId(crawlingCard.getId());
        dto.setCardNo(parseCardNo(crawlingCard.getCardNo()));
        dto.setRarity(parseCardRarity(crawlingCard.getRarity()));
        dto.setCardType(parseCardTypeEn(crawlingCard.getCardType()));
        dto.setLv(parseLv(crawlingCard.getLv(), dto.getCardType()));
        dto.setIsParallel(crawlingCard.getIsParallel());
        dto.setCardName(crawlingCard.getCardName());
        dto.setDp(parseDp(crawlingCard.getDP()));
        dto.setPlayCost(parsePlayCost(crawlingCard.getPlayCost()));
        dto.setEffect(parseEffect(crawlingCard.getEffect()));
        dto.setSourceEffect(parseSourceEffect(crawlingCard.getSourceEffect()));
        dto.setNote(parseNote(crawlingCard.getNote()));

        dto.setColor1(parseColor(crawlingCard.getColor1()));
        dto.setColor2(parseColor(crawlingCard.getColor2()));
        dto.setOriginUrl(parseEnUrl(crawlingCard.getImgUrl()));

        return dto;
    }

    private String parseCardNo(String cardNo) throws CardParseException {
        if (cardNo == null) {
            throw new CardParseException(CardParseExceptionMessage.NO_CARD_NO);
        }
        return cardNo;
    }

    private Rarity parseCardRarity(String rarity) throws CardParseException {
        if (rarity == null) {
            throw new CardParseException(CardParseExceptionMessage.WRONG_RARITY);
        }
        try {
            return Rarity.valueOf(rarity);
        } catch (IllegalArgumentException e) {
            throw new CardParseException(CardParseExceptionMessage.WRONG_RARITY);
        }
    }

    private CardType parseCardType(String cardType) throws CardParseException {
        if (cardType == null) {
            throw new CardParseException(CardParseExceptionMessage.WRONG_CARD_TYPE);
        }
        try {
            return CardType.findByKor(cardType);
        } catch (IllegalArgumentException e) {
            throw new CardParseException(CardParseExceptionMessage.WRONG_CARD_TYPE);
        }
    }

    private CardType parseCardTypeEn(String cardType) throws CardParseException {
        if (cardType == null) {
            throw new CardParseException(CardParseExceptionMessage.WRONG_CARD_TYPE);
        }
        try {
            return CardType.findByEng(cardType);
        } catch (IllegalArgumentException e) {
            throw new CardParseException(CardParseExceptionMessage.WRONG_CARD_TYPE);
        }
    }

    private Integer parseLv(String lv, CardType cardType) throws CardParseException {
        if (cardType.equals(CardType.OPTION) || cardType.equals(CardType.TAMER)) {
            return null;
        }
        if (lv == null) {
            throw new CardParseException(CardParseExceptionMessage.WRONG_LV);
        }
        String replace = lv.replace("Lv.", "");
        if (replace.equals("-")) {
            return 0;
        }
        return Integer.parseInt(replace);
    }

    private String parseCardName(String cardName) throws CardParseException {
        if (cardName == null) {
            throw new CardParseException(CardParseExceptionMessage.WRONG_CARD_NAME);
        }
        String[] cardNameStrings = cardName.split(" ");
        if (cardNameStrings.length < 2) {
            throw new CardParseException(CardParseExceptionMessage.WRONG_CARD_NAME);
        }
        StringBuilder cardNameStringBuilder = new StringBuilder();
        for (int i = 1; i < cardNameStrings.length; i++) {
            cardNameStringBuilder.append(cardNameStrings[i]);
            if (i != cardNameStrings.length - 1) {
                cardNameStringBuilder.append(" ");
            }
        }
        return cardNameStringBuilder.toString();
    }

    private Form parseForm(String form, CardType cardType) throws CardParseException {
        if (cardType.equals(CardType.OPTION) || cardType.equals(CardType.TAMER)) {
            return null;
        }
        if (form == null) {
            throw new CardParseException(CardParseExceptionMessage.WRONG_FORM);
        }
        try {
            return Form.findByKor(form);
        } catch (IllegalArgumentException e) {
            throw new CardParseException(CardParseExceptionMessage.WRONG_FORM);
        }
    }

    private String parseAttribute(String attribute) {
        if (attribute.equals("-")) {
            return null;
        }
        return attribute;
    }

    private List<String> parseTypes(String type, CardType cardType) throws CardParseException {
        if (cardType.equals(CardType.TAMER) || cardType.equals(CardType.OPTION)) {
            return new ArrayList<>();
        }
        if (type == null) {
            throw new CardParseException(CardParseExceptionMessage.WRONG_TYPE);
        }
        String[] types = type.split(",");
        List<String> typeList = new ArrayList<>();
        for (String s : types) {
            typeList.add(s.trim());
        }
        return typeList;
    }

    private Integer parseDp(String dp) throws CardParseException {
        if (dp.equals("-")) {
            return null;
        }
        try {
            double doubleValue = Double.parseDouble(dp);
            int intValue = (int) doubleValue;
            return Integer.valueOf(intValue);
        } catch (NumberFormatException e) {
            throw new CardParseException(CardParseExceptionMessage.WRONG_DP);
        }
    }
    private Integer parsePlayCost(String playCost) throws CardParseException {
        if (playCost.equals("-")) {
            return null;
        }
        try {
            return Integer.parseInt(playCost);
        } catch (NumberFormatException e) {
            throw new CardParseException(CardParseExceptionMessage.WRONG_PLAY_COST);
        }
    }

    private String parseEffect(String effect) {
        if (effect.equals("-")) {
            return null;
        }
        return effect;
    }

    private String parseSourceEffect(String sourceEffect) {
        if (sourceEffect.equals("-")) {
            return null;
        }
        return sourceEffect;
    }

    private String parseNote(String note) {
        return note.replace("▹", "");
    }

    private Color parseColor(String color) throws CardParseException {
        if (color == null) {
            return null;
        }
        try {
            return Color.getColorByString(color);
        } catch (IllegalArgumentException e) {
            throw new CardParseException(CardParseExceptionMessage.WRONG_COLOR);
        }
    }

    private String parseUrl(String imgUrl) throws CardParseException {
        if (imgUrl == null) {
            throw new CardParseException(CardParseExceptionMessage.NO_IMG_URL);
        }
        return imgUrl;
    }

    private String parseEnUrl(String imgUrl) throws CardParseException {
        if (imgUrl == null) {
            throw new CardParseException(CardParseExceptionMessage.NO_IMG_URL);
        }
        if (imgUrl.startsWith("..")) {
            imgUrl = imgUrl.substring(2);
        }
        return imgUrl;
    }

    private void setDtoDigivolve(ReflectCardRequestDto dto, CrawlingCardEntity crawlingCard) throws CardParseException {
        Digivolve digivolve1 = parseDigivolve(crawlingCard.getDigivolveCost1());
        if (digivolve1 != null) {
            dto.setDigivolveCondition1(digivolve1.digivolveCondition);
            dto.setDigivolveCost1(digivolve1.digivolveCost);
        }
        Digivolve digivolve2 = parseDigivolve(crawlingCard.getDigivolveCost2());
        if (digivolve2 != null) {
            dto.setDigivolveCondition2(digivolve2.digivolveCondition);
            dto.setDigivolveCost2(digivolve2.digivolveCost);
        }


    }

    private Digivolve parseDigivolve( String digivolveString) throws CardParseException {
        if (digivolveString == null||digivolveString.equals("-")) {
            return null;
        }
        String[] digivolveStrings = digivolveString.split("~");
        if (digivolveStrings.length < 2) {
            throw new CardParseException(CardParseExceptionMessage.WRONG_DIGIVOLVE);
        }
        try {

            int digivolveCondition = Integer.parseInt(digivolveStrings[0].replace("Lv.", ""));
            double digivolveDoubleCost = Double.parseDouble(digivolveStrings[1]);
            int digivolveCost = (int) digivolveDoubleCost;
            return new Digivolve(digivolveCost, digivolveCondition);
        } catch (NumberFormatException e) {
            throw new CardParseException(CardParseExceptionMessage.WRONG_DIGIVOLVE);
        }


    }
    @Getter
    @AllArgsConstructor
    private static class Digivolve{
        Integer digivolveCost;
        Integer digivolveCondition;
    }
}
