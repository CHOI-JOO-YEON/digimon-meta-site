package com.joo.digimon.global.enums;

public enum Color {
    RED, BLUE, YELLOW, GREEN, BLACK, PURPLE, WHITE;

    public static Color getColorByString(String colorString) {
        for (Color value : Color.values()) {
            if(colorString.toUpperCase().equals(value.name())){
                return value;
            }
        }
        throw new IllegalArgumentException("잘못된 색깔 문자열");
    }
}
