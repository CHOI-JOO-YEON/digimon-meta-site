package com.joo.digimon.card.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.joo.digimon.global.enums.CardType;
import com.joo.digimon.global.enums.Color;
import com.joo.digimon.global.enums.Form;
import com.joo.digimon.global.enums.Rarity;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;

@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class CardAdminPutDto {
    Integer cardId;
    String cardNo;
    String cardName;
    String cardEngName;
    Integer lv;
    Integer dp;
    Integer playCost;
    Integer digivolveCost1;
    Integer digivolveCondition1;
    Integer digivolveCost2;
    Integer digivolveCondition2;
    String effect;
    String engEffect;
    String sourceEffect;
    String engSourceEffect;
    Color color1;
    Color color2;
    Color color3;
    Rarity rarity;
    CardType cardType;
    Form form;
    String attribute;
    Set<String> types = new HashSet<>();
    String imgUrl;
    String smallImgUrl;
    Boolean isParallel;
    String sortString;
    String noteName;
    Integer noteId;
    LocalDate releaseDate;
    Boolean isEn;
}
