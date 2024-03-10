package com.joo.digimon.deck.service;

import com.joo.digimon.card.model.CardImgEntity;
import com.joo.digimon.card.repository.CardImgRepository;
import com.joo.digimon.crawling.enums.CardType;
import com.joo.digimon.deck.dto.*;
import com.joo.digimon.deck.model.DeckCardEntity;
import com.joo.digimon.deck.model.DeckEntity;
import com.joo.digimon.deck.model.Format;
import com.joo.digimon.deck.repository.DeckCardRepository;
import com.joo.digimon.deck.repository.DeckRepository;
import com.joo.digimon.deck.repository.FormatRepository;
import com.joo.digimon.exception.model.ForbiddenAccessException;
import com.joo.digimon.user.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeckServiceImpl implements DeckService {
    private final DeckRepository deckRepository;
    private final DeckCardRepository deckCardRepository;
    private final CardImgRepository cardImgRepository;
    private final FormatRepository formatRepository;

    @Value("${domain.url}")
    private String prefixUrl;

    @Value("${back.digimon}")
    private String backDigimonUrl;

    @Value("${back.digitama}")
    private String backDigitamaUrl;

    @Override
    @Transactional
    public ResponseDeckDto postDeck(RequestDeckDto requestDeckDto, User user) {
        Format format = formatRepository.findById(requestDeckDto.getFormatId()).orElseThrow();
        DeckEntity deck = Optional.ofNullable(requestDeckDto.getDeckId())
                .flatMap(deckRepository::findById)
                .orElseGet(() -> deckRepository.save(DeckEntity.builder()
                        .user(user).deckCardEntities(new HashSet<>()).build()));

        deck.updateDeckMetaData(requestDeckDto, format);
        updateDeckCards(requestDeckDto, deck, format);
        return new ResponseDeckDto(deck, prefixUrl);
    }

    @Transactional
    public void updateDeckCards(RequestDeckDto requestDeckDto, DeckEntity deck, Format format) {
        List<Integer> cardIds = new ArrayList<>(requestDeckDto.getCardAndCntMap().keySet());
        Map<Integer, CardImgEntity> cardImgMap = cardImgRepository.findAllById(cardIds)
                .stream()
                .collect(Collectors.toMap(CardImgEntity::getId, Function.identity()));
        Optional<CardImgEntity> latestReleaseCard = cardImgMap.values()
                .stream()
                .max(Comparator.comparing(cardImg -> cardImg.getCardEntity().getReleaseDate()));

        if (latestReleaseCard.isPresent()) {
            LocalDate cardReleaseDate = latestReleaseCard.get().getCardEntity().getReleaseDate();
            if (cardReleaseDate.isAfter(format.getEndDate())) {
                throw new IllegalArgumentException();
            }
        }


        Map<Integer, DeckCardEntity> currentCards = new HashMap<>();
        deck.getDeckCardEntities().forEach(card -> currentCards.put(card.getCardImgEntity().getId(), card));

        requestDeckDto.getCardAndCntMap().forEach((id, cnt) -> {
            if (currentCards.containsKey(id)) {
                DeckCardEntity card = currentCards.get(id);
                card.updateCnt(cnt);
                currentCards.remove(id);
            } else {
                CardImgEntity imgEntity = cardImgMap.get(id);
                if (imgEntity != null) {
                    deck.addDeckCardEntity(deckCardRepository.save(DeckCardEntity.builder()
                            .cardImgEntity(imgEntity)
                            .deckEntity(deck)
                            .cnt(cnt)
                            .build()));
                }
            }
        });
        deckCardRepository.deleteAll(currentCards.values());
        currentCards.values().forEach(deck.getDeckCardEntities()::remove);

    }

    @Override
    public List<DeckSummaryResponseDto> getDecks(DeckSearchParameter deckSearchParameter, User user) {
        List<DeckSummaryResponseDto> deckSummaryResponseDtoList = new ArrayList<>();
        if (deckSearchParameter.getIsMyDeck()) {
            List<DeckEntity> myDeckList = deckRepository.findByUserOrderByCreatedDateTime(user);
            for (DeckEntity deck : myDeckList) {
                deckSummaryResponseDtoList.add(new DeckSummaryResponseDto(deck));
            }
            return deckSummaryResponseDtoList;
        }
        List<DeckEntity> decks = deckRepository.findByIsPublicIsTrue();
        for (DeckEntity deck : decks) {
            deckSummaryResponseDtoList.add(new DeckSummaryResponseDto(deck));
        }


        return deckSummaryResponseDtoList;
    }

    @Override
    public ResponseDeckDto findDeck(Integer id, User user) {
        DeckEntity deck = deckRepository.findById(id).orElseThrow();
        if (!deck.getIsPublic() && !deck.getUser().equals(user)) {
            throw new ForbiddenAccessException();
        }
        return new ResponseDeckDto(deck, prefixUrl);
    }

    @Override
    public ResponseDeckDto importDeck(DeckImportRequestDto deckImportRequestDto) {
        Map<String, Integer> map = deckImportRequestDto.getDeck();

        ResponseDeckDto responseDeckDto = new ResponseDeckDto();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            Optional<CardImgEntity> cardImg = cardImgRepository.findByCardEntityCardNoAndIsParallelFalse(entry.getKey());
            cardImg.ifPresent(cardImgEntity -> responseDeckDto.addCard(cardImgEntity, entry.getValue(), prefixUrl));
        }


        return responseDeckDto;
    }

    @Override
    public TTSDeckFileDto exportTTSDeck(RequestDeckDto requestDeckDto) {
        TTSDeckFileDto ttsDeckFileDto = new TTSDeckFileDto();

        int index = 1;
        List<CardImgEntity> digitamas = new ArrayList<>();
        for (Map.Entry<Integer, Integer> integerEntry : requestDeckDto.getCardAndCntMap().entrySet()) {
            try {
                CardImgEntity cardImgEntity = cardImgRepository.findById(integerEntry.getKey()).orElseThrow();
                if (cardImgEntity.getCardEntity().getCardType().equals(CardType.DIGITAMA)) {
                    digitamas.add(cardImgEntity);
                    continue;
                }
                for (int i = 0; i < integerEntry.getValue(); i++) {
                    ttsDeckFileDto.addCard(index++, prefixUrl + cardImgEntity.getUploadUrl(), backDigimonUrl);
                }
            } catch (Exception ignore) {
            }
        }

        for (CardImgEntity digitama : digitamas) {
            try {
                ttsDeckFileDto.addCard(index++, prefixUrl + digitama.getUploadUrl(), backDigitamaUrl);
            } catch (Exception ignore) {

            }
        }

        return ttsDeckFileDto;
    }

}
