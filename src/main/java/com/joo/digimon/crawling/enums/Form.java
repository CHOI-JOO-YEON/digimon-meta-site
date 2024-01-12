package com.joo.digimon.crawling.enums;

import lombok.Getter;

@Getter
public enum Form {
    IN_TRAINING("In-Training","유년기"),
    BABY("Baby","유년기"),
    ROOKIE("Rookie","성장기"),
    CHAMPION("Champion","성숙기"),
    ULTIMATE("Ultimate","완전체"),
    MEGA("Mega","궁극체"),
    ARMOR("Armor Form","아머체"),
    D_REAPER("D-Reaper","데리퍼"),
    UNKNOWN("Unknown","불명"),
    HYBRID("Hybrid","하이브리드체");


    private final String eng;
    private final String kor;
    Form(String eng, String kor) {
        this.eng = eng;
        this.kor=kor;
    }


    public static String findKoreanNameByEnglishName(String englishName) {
        for (Form form : Form.values()) {
            if (form.getEng().equalsIgnoreCase(englishName)) {
                return form.getKor();
            }
        }
        throw new IllegalArgumentException("No corresponding Korean name found for: " + englishName);
    }
}
