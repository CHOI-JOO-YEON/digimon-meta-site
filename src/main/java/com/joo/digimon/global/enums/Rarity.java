package com.joo.digimon.global.enums;

public enum Rarity {
    C, U, R, SR, SEC, P, ERROR;

    public static Rarity parseRarity(String rarity) {
        for (Rarity r : Rarity.values()) {
            if (r.name().equalsIgnoreCase(rarity)) {
                return r;
            }
        }
        return ERROR;
    }
}
