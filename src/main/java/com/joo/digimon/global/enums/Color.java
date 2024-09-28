package com.joo.digimon.global.enums;

public enum Color {
    RED, BLUE, YELLOW, GREEN, BLACK, PURPLE, WHITE
    ,ERROR
    ;

    public static Color getColorByString(String colorString) {
        for (Color value : Color.values()) {
            if(colorString.toUpperCase().equals(value.name())){
                return value;
            }
        }
       return ERROR;
    }
}
