package com.joo.digimon.card.dto.card;

import com.joo.digimon.global.enums.Locale;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LocaleCardData {
    private String name;
    private String effect;
    private String sourceEffect;
    private Locale locale;
    private String imgUrl;
    private String smallImgUrl;
}
