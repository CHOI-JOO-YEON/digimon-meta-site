package com.joo.digimon.card.dto.card;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.joo.digimon.global.enums.*;
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
    String cardJpnName;
    Integer lv;
    Integer dp;
    Integer playCost;
    Integer digivolveCost1;
    Integer digivolveCondition1;
    Integer digivolveCost2;
    Integer digivolveCondition2;
    String effect;
    String engEffect;
    String jpnEffect;
    String sourceEffect;
    String engSourceEffect;
    String jpnSourceEffect;
    Color color1;
    Color color2;
    Color color3;
    Rarity rarity;
    CardType cardType;
    Form form;
    Attribute attribute;
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
