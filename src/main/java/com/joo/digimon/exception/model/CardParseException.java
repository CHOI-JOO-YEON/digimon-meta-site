package com.joo.digimon.exception.model;

import com.joo.digimon.exception.message.CardParseExceptionMessage;

public class CardParseException extends Exception{
    public CardParseException(CardParseExceptionMessage cardParseExceptionMessage) {
        super(cardParseExceptionMessage.name());
    }
}
