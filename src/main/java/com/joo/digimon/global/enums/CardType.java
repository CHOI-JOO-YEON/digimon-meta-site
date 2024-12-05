package com.joo.digimon.global.enums;

import lombok.Getter;

@Getter
public enum CardType {
    DIGITAMA("디지타마", "Digi-Egg", "デジタマ"),
    DIGIMON("디지몬", "Digimon", "デジモン"),
    TAMER("테이머", "Tamer", "テイマー"),
    OPTION("옵션", "Option", "オプション"),
    ERROR("에러", "Error", "Error");

    final String kor;
    final String eng;
    final String jpn;

    CardType(String kor, String eng, String jpn) {
        this.kor = kor;
        this.eng = eng;
        this.jpn = jpn;
    }

    public static CardType findByString(String cardType, Locale locale) {
        for (CardType value : CardType.values()) {
            if (locale == Locale.KOR) {
                if (value.kor.equals(cardType)) {
                    return value;
                }
            } else if (locale == Locale.ENG) {
                if (value.eng.equals(cardType)) {
                    return value;
                }
            } else if (locale == Locale.JPN) {
                if (value.jpn.equals(cardType)) {
                    return value;
                }
            }

        }
        return ERROR;
    }

}
