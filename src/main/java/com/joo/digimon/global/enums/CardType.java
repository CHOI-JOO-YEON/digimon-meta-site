package com.joo.digimon.global.enums;

import lombok.Getter;

@Getter
public enum CardType {
    DIGITAMA("디지타마", "Digi-Egg"), DIGIMON("디지몬", "Digimon"), TAMER("테이머", "Tamer"), OPTION("옵션", "Option"), ERROR("에러", "Error");

    final String kor;
    final String eng;

    CardType(String kor, String eng) {
        this.kor = kor;
        this.eng = eng;

    }

    public static CardType findByString(String cardType, String locale) {
        for (CardType value : CardType.values()) {
            if (locale.equals("KOR")) {
                if (value.kor.equals(cardType)) {
                    return value;
                }
            } else if (locale.equals("ENG")) {
                if (value.eng.equals(cardType)) {
                    return value;
                }
            }

        }
        return ERROR;
    }

}
