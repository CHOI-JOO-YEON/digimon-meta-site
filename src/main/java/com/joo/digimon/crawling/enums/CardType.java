package com.joo.digimon.crawling.enums;

import lombok.Getter;

@Getter
public enum CardType {
    DIGITAMA("디지타마"), DIGIMON("디지몬"), TAMER("테이머"), OPTION("옵션"), ;

    String kor;

    CardType(String kor) {
        this.kor = kor;
    }

    public static CardType findByKor(String kor) {
        for (CardType value : CardType.values()) {
            if (value.kor.equals(kor)) {
                return value;
            }
        }
        throw new IllegalArgumentException("잘못된 한글 카드타입");
    }

}
