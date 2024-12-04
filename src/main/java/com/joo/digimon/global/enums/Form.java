package com.joo.digimon.global.enums;

import lombok.Getter;

@Getter
public enum Form {
    IN_TRAINING("In-Training", "유년기" ,"幼年期"),
    BABY("Baby", "유년기", "幼年期"),
    ROOKIE("Rookie", "성장기", "成長期"),
    CHAMPION("Champion", "성숙기", "成熟期"),
    ULTIMATE("Ultimate", "완전체", "完全体"),
    MEGA("Mega", "궁극체", "究極体"),
    ARMOR("Armor Form", "아머체", "アーマー体"),
    D_REAPER("D-Reaper", "디・리퍼", "デ・リーパー"),
    UNKNOWN("Unknown", "불명", "不明"),
    HYBRID("Hybrid", "하이브리드체", "ハイブリッド体"),
    ERROR("Error", "에러", "Error"),
    ;


    private final String eng;
    private final String kor;
    private final String jpn;

    Form(String eng, String kor, String jpn) {
        this.eng = eng;
        this.kor = kor;
        this.jpn = jpn;
    }

    public static Form findForm(String form, String locale) {
        for (Form value : Form.values()) {
            if (locale.equals("ENG")) {
                if (value.eng.equals(form)) {
                    return value;
                }
            } else if (locale.equals("KOR")) {
                if (value.kor.equals(form)) {
                    return value;
                }
            } else if (locale.equals("JPN")) {
                if (value.jpn.equals(form)) {
                    return value;
                }
            }
        }
        return Form.ERROR;
    }

    public static String findKoreanNameByEnglishName(String englishName) {
        for (Form form : Form.values()) {
            if (form.getEng().equalsIgnoreCase(englishName)) {
                return form.getKor();
            }
        }
        throw new IllegalArgumentException("No corresponding Korean name found for: " + englishName);
    }

    public static Form findByKor(String kor) {
        for (Form value : Form.values()) {
            if (value.kor.equals(kor)) {
                return value;
            }
        }
        throw new IllegalArgumentException("잘못된 한글 형태");
    }
}
