package com.joo.digimon.global.exception.model;

import com.joo.digimon.global.exception.message.CardParseExceptionMessage;

public class CardParseException extends Exception{
    public CardParseException(CardParseExceptionMessage cardParseExceptionMessage) {
        super(cardParseExceptionMessage.name());
    }
}
