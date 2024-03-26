package com.joo.digimon.util;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SpecialLimitCard {

    private static Map<String,Integer> cardMap;

    static {
        cardMap = new HashMap<>();
        cardMap.put("BT6-085", 50);
        cardMap.put("EX2-046", 50);
    }
    public static int getCardLimit(String cardNo){
        if (cardMap.containsKey(cardNo)) {
            return cardMap.get(cardNo);
        }
        return 4;
    }
}
