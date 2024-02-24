package com.joo.digimon.deck.service;

import com.joo.digimon.card.model.CardImgEntity;
import com.joo.digimon.card.repository.CardImgRepository;
import com.joo.digimon.deck.dto.DeckSearchParameters;
import com.joo.digimon.deck.dto.RequestDeckDto;
import com.joo.digimon.deck.dto.ResponseDeckDto;
import com.joo.digimon.deck.model.DeckCardEntity;
import com.joo.digimon.deck.model.DeckEntity;
import com.joo.digimon.deck.repository.DeckCardRepository;
import com.joo.digimon.deck.repository.DeckRepository;
import com.joo.digimon.user.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeckServiceImpl implements DeckService {
    private final DeckRepository deckRepository;
    private final DeckCardRepository deckCardRepository;
    private final CardImgRepository cardImgRepository;

    @Value("${domain.url}")
    private String prefixUrl;

    @Override
    @Transactional
    public ResponseDeckDto postDeck(RequestDeckDto requestDeckDto, User user) {
        DeckEntity deck = Optional.ofNullable(requestDeckDto.getDeckId())
                .flatMap(deckRepository::findById)
                .orElseGet(() -> deckRepository.save(DeckEntity.builder().user(user).deckCardEntities(new HashSet<>()).build()));

        if (!deck.getDeckCardEntities().isEmpty()) {
            deckCardRepository.deleteAll(deck.getDeckCardEntities());
            deck.getDeckCardEntities().clear(); // 컬렉션 비우기
        }

        List<DeckCardEntity> deckCardEntities = requestDeckDto.getCardAndCntMap().entrySet().stream()
                .map(entry -> cardImgRepository.findById(entry.getKey())
                        .map(cardImgEntity -> DeckCardEntity.builder().deckEntity(deck).cardImgEntity(cardImgEntity).cnt(entry.getValue()).build())
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        deckCardEntities.forEach(deck::addDeckCardEntity); // deck에 DeckCardEntity 추가
        deckCardRepository.saveAll(deckCardEntities); // DeckCardEntity 저장

        return new ResponseDeckDto(deck,prefixUrl);
    }

    @Override
    public List<RequestDeckDto> getDecks(DeckSearchParameters deckSearchParameters) {
        return null;
    }
}
