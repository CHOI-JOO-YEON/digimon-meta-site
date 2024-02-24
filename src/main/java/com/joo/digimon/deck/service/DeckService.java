package com.joo.digimon.deck.service;

import com.joo.digimon.deck.dto.DeckSearchParameters;
import com.joo.digimon.deck.dto.RequestDeckDto;
import com.joo.digimon.deck.dto.ResponseDeckDto;
import com.joo.digimon.user.model.User;

import java.util.List;

public interface DeckService {
    ResponseDeckDto postDeck(RequestDeckDto requestDeckDto, User user);
    List<RequestDeckDto> getDecks(DeckSearchParameters deckSearchParameters);
}
