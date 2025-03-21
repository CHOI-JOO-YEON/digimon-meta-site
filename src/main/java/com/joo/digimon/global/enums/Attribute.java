package com.joo.digimon.global.enums;

import lombok.Getter;

public enum Attribute {
    백신종("Vaccine", "ワクチン種", "백신종"),
    데이터종("Data", "データ種", "데이터종"),
    바이러스종("Virus","ウィルス種", "바이러스종"),
    프리("Free", "フリー", "프리"),
    불명("Unknown", "不明", "불명"),
    배리어블종("Variable", "ヴァリアブル種", "배리어블종"),
    NODATA("NO DATA", "NODATA", "NO DATA")
    ;

    private final String eng;
    private final String jpn;
    @Getter
    private final String kor;

    Attribute(String eng, String jpn, String kor) {
        this.eng = eng;
        this.jpn = jpn;
        this.kor = kor;
    }

    public static Attribute findByString(String attribute, Locale locale) {
        for (Attribute value : Attribute.values()) {
            if (locale == Locale.KOR) {
                if (value.kor.equals(attribute)) {
                    return value;
                }
            } else if (locale == Locale.ENG) {
                if (value.eng.equals(attribute)) {
                    return value;
                }
            } else if (locale == Locale.JPN) {
                if (value.jpn.equals(attribute)) {
                    return value;
                }
            }

        }
        return 불명;
    }
}
