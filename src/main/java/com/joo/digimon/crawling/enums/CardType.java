package com.joo.digimon.crawling.enums;

import lombok.Getter;

@Getter
public enum CardType {
    DIGITAMA("디지타마","Digi-Egg"), DIGIMON("디지몬","Digimon"), TAMER("테이머","Tamer"), OPTION("옵션","Option"), ;

    final String kor;
    final String eng;

    CardType(String kor, String eng) {
        this.kor = kor;
        this.eng = eng;

    }

    public static CardType findByKor(String kor) {
        for (CardType value : CardType.values()) {
            if (value.kor.equals(kor)) {
                return value;
            }
        }
        throw new IllegalArgumentException("잘못된 한글 카드타입");
    }

    public static CardType findByEng(String eng) {
        for (CardType value : CardType.values()) {
            if (value.eng.equals(eng)) {
                return value;
            }
        }
        throw new IllegalArgumentException("잘못된 영어 카드타입");
    }

}
