package com.joo.digimon.deck.service;

import com.joo.digimon.deck.dto.DeckSearchParameter;
import com.joo.digimon.deck.dto.DeckSummaryResponseDto;
import com.joo.digimon.deck.dto.RequestDeckDto;
import com.joo.digimon.deck.dto.ResponseDeckDto;
import com.joo.digimon.user.model.User;

import java.util.List;

public interface DeckService {
    ResponseDeckDto postDeck(RequestDeckDto requestDeckDto, User user);
    List<DeckSummaryResponseDto> getDecks(DeckSearchParameter deckSearchParameter, User user);

    ResponseDeckDto findDeck(Integer id, User user);
}
