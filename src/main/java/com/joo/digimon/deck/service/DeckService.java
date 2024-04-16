package com.joo.digimon.deck.service;

import com.joo.digimon.deck.dto.*;
import com.joo.digimon.user.model.User;
import org.springframework.http.HttpStatusCode;

import java.util.List;

public interface DeckService {
    ResponseDeckDto postDeck(RequestDeckDto requestDeckDto, User user);

    ResponseDeckDto findDeck(Integer id, User user);

    ResponseDeckDto importDeck(DeckImportRequestDto deckImportRequestDto);
    ResponseDeckDto importDeck(DeckImportThisSiteRequestDto deckImportThisSiteRequestDto);

    TTSDeckFileDto exportTTSDeck(RequestDeckDto requestDeckDto);

    PagedResponseDeckDto finMyDecks(User user, DeckSearchParameter deckSearchParameter);

    PagedResponseDeckDto findDecks(DeckSearchParameter deckSearchParameter);

    void deleteDeck(Integer deckId, User user);

    List<ResponseDeckDto> findAllMyDeck(User user);
}
