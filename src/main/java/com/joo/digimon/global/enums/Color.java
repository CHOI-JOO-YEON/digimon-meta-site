package com.joo.digimon.global.enums;

public enum Color {
    RED("赤"), BLUE("青"), YELLOW("黄"), GREEN("緑"), BLACK("黒"), PURPLE("紫"), WHITE("白")
    ,ERROR("error")
    ;

    private final String jpn;

    Color(String jpn) {
        this.jpn = jpn;
    }
    public static Color getColorByString(String colorString) {
        for (Color value : Color.values()) {
            if(colorString.toUpperCase().equals(value.name())){
                return value;
            }
        }
       return ERROR;
    }

    public static Color getColorByJpn(String jpn) {
        for (Color value : Color.values()) {
            if(jpn.equals(value.jpn)){
                return value;
            }
        }
        return ERROR;
    }

    public String getJpn() {
        return jpn;
    }
}
