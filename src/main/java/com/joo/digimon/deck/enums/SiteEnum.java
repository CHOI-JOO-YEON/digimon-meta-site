package com.joo.digimon.deck.enums;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Getter
public enum SiteEnum {

    TTS("tts") {
        @Override
        public List<String> deckCodeParsing(String deckCode) {
            ObjectMapper objectMapper = new ObjectMapper();
            List<String> parsedDeck = new ArrayList<>();
            List<String> readStrings = new ArrayList<>();
            try {
                readStrings = objectMapper.readValue(deckCode, List.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            for (int i = 1; i < readStrings.size(); i++) {
                String cardCode = readStrings.get(i);
                if (cardCode.matches(".*\\D$")) {
                    cardCode = cardCode.substring(0, cardCode.length() - 2);
                }
                parsedDeck.add(cardCode);
            }

            return parsedDeck;
        }

        @Override
        public String cardCodeListToSiteCode(List<String> deck) throws JsonProcessingException {
            List<String> returnStrings = new ArrayList<>();
            returnStrings.add("Exported from Joo");
            deck.forEach(card -> returnStrings.add(card));
            ObjectMapper objectMapper = new ObjectMapper();

            return objectMapper.writeValueAsString(returnStrings);
        }
    },
    Digimon_Meta("digimon-meta") {
        @Override
        public List<String> deckCodeParsing(String deckCode) {
            List<String> cards = new ArrayList<>();

            deckCode = deckCode.replaceAll("\\x08", "");

            String[] cardItems = deckCode.split("a");
            Pattern pattern = Pattern.compile("(\\d+)([a-zA-Z0-9-]+)");
            for (String cardItem : cardItems) {
                String[] as = cardItem.split("n");
                Integer cnt = Integer.parseInt(as[0]);
                String cardNo = as[1];
                if (cardNo.matches(".*\\D$")) {
                    cardNo = cardNo.substring(0, cardNo.length() - 2);
                }
                for (int i = 0; i < cnt; i++) {
                    cards.add(cardNo);
                }
            }

            return cards;
        }

        @Override
        public String cardCodeListToSiteCode(List<String> deck) throws JsonProcessingException {
            return null;
        }
    },

    DIGIMON_CARD_DEV("digimoncard-dev") {
        @Override
        public List<String> deckCodeParsing(String deckCode) {
            List<String> parsedDeck = new ArrayList<>();
            String[] lines = deckCode.split("\n");

            for (String line : lines) {
                if (line.trim().isEmpty() || line.trim().startsWith("//")) {
                    continue;
                }

                String[] parts = line.trim().split("\\s+");

                // 각 줄에서 카드 매수와 카드 번호를 추출합니다.
                if (parts.length >= 2) {
                    int cardCount = Integer.parseInt(parts[0]);
                    String cardNo = parts[parts.length - 1];
                    if (cardNo.matches(".*\\D$")) {
                        cardNo = cardNo.substring(0, cardNo.length() - 2);
                    }
                    for (int i = 0; i < cardCount; i++) {
                        parsedDeck.add(cardNo);
                    }

                }
            }
            return parsedDeck;
        }

        @Override
        public String cardCodeListToSiteCode(List<String> deck) throws JsonProcessingException {
            HashMap<String, Integer> map = new HashMap<>();
            StringBuilder stringBuilder = new StringBuilder();
            for (String string : deck) {
                map.put(string, map.getOrDefault(string, 0) + 1);
            }
            for (Map.Entry<String, Integer> stringIntegerEntry : map.entrySet()) {
                stringBuilder.append(stringIntegerEntry.getValue()).append(" ").append(stringIntegerEntry.getKey()).append("\n");

            }
            return stringBuilder.toString();
        }
    };
    String siteName;


    public abstract List<String> deckCodeParsing(String deckCode);

    public abstract String cardCodeListToSiteCode(List<String> deck) throws JsonProcessingException;

    SiteEnum(String siteName) {
        this.siteName = siteName;
    }

    public static SiteEnum fromSiteName(String siteName) {
        for (SiteEnum siteEnum : values()) {
            if (siteEnum.getSiteName().equals(siteName)) {
                return siteEnum;
            }
        }
        throw new IllegalArgumentException("No enum constant with siteName: " + siteName);
    }

    public static String deckCodeToOtherSiteDeckCode(SiteEnum start, SiteEnum end, String deckCode) throws JsonProcessingException {
        return end.cardCodeListToSiteCode(start.deckCodeParsing(deckCode));
    }

    public HashMap<String, Integer> getCardNoAndCountMap(String deckCode) {
        List<String> cardNoList = deckCodeParsing(deckCode);
        HashMap<String, Integer> cardMap = new HashMap<>();
        for (String cardNo : cardNoList) {
            cardMap.put(cardNo, cardMap.getOrDefault(cardNo, 0) + 1);
        }
        return cardMap;

    }

}
