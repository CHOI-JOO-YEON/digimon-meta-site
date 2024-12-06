package com.joo.digimon.global.enums;

public enum Attribute {
    백신종("Vaccine", "ワクチン種"),
    데이터종("Data", "データ種"),
    바이러스종("Virus","ウィルス種"),
    프리("Free", "フリー"),
    불명("Unknown", "不明"),
    배리어블종("Variable", "ヴァリアブル種"),
    ;

    private final String eng;
    private final String jpn;

    Attribute(String eng, String jpn) {
        this.eng = eng;
        this.jpn = jpn;
    }

    public static Attribute findByString(String attribute, Locale locale) {
        for (Attribute value : Attribute.values()) {
            if (locale == Locale.KOR) {
                if (value.name().equals(attribute)) {
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
