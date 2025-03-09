package com.joo.digimon.deck.service;

import com.joo.digimon.deck.dto.*;
import com.joo.digimon.user.model.User;

import java.util.List;
import java.util.Map;

public interface DeckService {
    ResponseDeckDto postDeck(RequestDeckDto requestDeckDto, User user);

    ResponseDeckDto findDeck(Integer id, User user);

    ResponseDeckDto importDeck(DeckImportRequestDto deckImportRequestDto);
    ResponseDeckDto importDeck(DeckImportThisSiteRequestDto deckImportThisSiteRequestDto);

    ResponseDeckDto importDeckByDeckMap(Map<Integer, Integer> map);

    TTSDeckFileDto exportTTSDeck(RequestDeckDto requestDeckDto);

    PagedResponseDeckDto finMyDecks(User user, DeckSearchParameter deckSearchParameter);

    PagedResponseDeckDto  findDecks(DeckSearchParameter deckSearchParameter, User user);

    void deleteDeck(Integer deckId, User user);

    List<ResponseDeckDto> findAllMyDeck(User user);

    FormatDeckCountResponseDto getFormatDeckCount(User user);
}
