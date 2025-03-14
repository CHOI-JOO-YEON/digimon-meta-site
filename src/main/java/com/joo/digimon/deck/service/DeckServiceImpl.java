package com.joo.digimon.deck.service;

import com.joo.digimon.card.model.CardEntity;
import com.joo.digimon.card.model.CardImgEntity;
import com.joo.digimon.card.repository.CardImgRepository;
import com.joo.digimon.global.enums.CardType;
import com.joo.digimon.global.enums.Color;
import com.joo.digimon.deck.dto.*;
import com.joo.digimon.deck.model.*;
import com.joo.digimon.deck.repository.DeckCardRepository;
import com.joo.digimon.deck.repository.DeckColorRepository;
import com.joo.digimon.deck.repository.DeckRepository;
import com.joo.digimon.deck.repository.FormatRepository;
import com.joo.digimon.global.exception.model.ForbiddenAccessException;
import com.joo.digimon.global.exception.model.UnAuthorizationException;
import com.joo.digimon.limit.model.LimitCardEntity;
import com.joo.digimon.limit.model.LimitEntity;
import com.joo.digimon.limit.repository.LimitRepository;
import com.joo.digimon.user.enums.Role;
import com.joo.digimon.user.model.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@RefreshScope
public class DeckServiceImpl implements DeckService {
    private final DeckRepository deckRepository;
    private final DeckCardRepository deckCardRepository;
    private final CardImgRepository cardImgRepository;
    private final FormatRepository formatRepository;
    private final DeckColorRepository deckColorRepository;
    private final LimitRepository limitRepository;
    private final EntityManager entityManager;

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
        updateDeckColors(requestDeckDto.getColors(), deck);
        deck.updateDeckValid();
        return new ResponseDeckDto(deck);
    }

    private void updateDeckColors(List<Color> colors, DeckEntity deck) {

        List<DeckColor> addColorList = new ArrayList<>();
        List<DeckColor> removeColorList = new ArrayList<>();
        if (deck.getDeckColors() == null) {
            for (Color color : colors) {
                deck.addDeckColor(DeckColor.builder()
                        .color(color)
                        .deckEntity(deck)
                        .build());
            }
            deckColorRepository.saveAll(deck.getDeckColors());
            return;
        }

        Set<Color> alreadyReflectColors = deck.getDeckColors().stream().map(DeckColor::getColor).collect(Collectors.toSet());
        for (DeckColor deckColor : deck.getDeckColors()) {
            if (!colors.contains(deckColor.getColor())) {
                removeColorList.add(deckColor);
            }
        }
        for (Color color : colors) {
            if (!alreadyReflectColors.contains(color)) {
                addColorList.add(DeckColor.builder()
                        .color(color)
                        .deckEntity(deck)
                        .build());
            }
        }

        deckColorRepository.deleteAll(removeColorList);
        deckColorRepository.saveAll(addColorList);
    }

    @Transactional
    public void updateDeckCards(RequestDeckDto requestDeckDto, DeckEntity deck, Format format) {
        List<Integer> cardIds = new ArrayList<>(requestDeckDto.getCardAndCntMap().keySet());
        Map<Integer, CardImgEntity> cardImgMap = cardImgRepository.findAllById(cardIds)
                .stream()
                .collect(Collectors.toMap(CardImgEntity::getId, Function.identity()));
        Optional<CardImgEntity> latestReleaseCard = cardImgMap.values()
                .stream()
                .max(Comparator.comparing(cardImg -> cardImg.getCardEntity().getReleaseDate(), Comparator.nullsLast(Comparator.naturalOrder())));

        if (latestReleaseCard.isPresent()) {
            LocalDate cardReleaseDate = latestReleaseCard.get().getCardEntity().getReleaseDate();

            if (cardReleaseDate != null && cardReleaseDate.isAfter(format.getEndDate())) {
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
    public ResponseDeckDto findDeck(Integer id, User user) {
        DeckEntity deck = deckRepository.findById(id).orElseThrow();
        if (!deck.getIsPublic() && !deck.getUser().equals(user)) {
            throw new ForbiddenAccessException();
        }
        return new ResponseDeckDto(deck);
    }

    @Override
    public ResponseDeckDto importDeck(DeckImportRequestDto deckImportRequestDto) {
//        Map<String, Integer> map = deckImportRequestDto.getDeck();
//
//        ResponseDeckDto responseDeckDto = new ResponseDeckDto();
//        for (Map.Entry<String, Integer> entry : map.entrySet()) {
//            Optional<CardImgEntity> cardImg = cardImgRepository.findByCardEntityCardNoAndIsParallelFalse(entry.getKey());
//            cardImg.ifPresent(cardImgEntity -> responseDeckDto.addCard(cardImgEntity, entry.getValue(), prefixUrl));
//        }
//
//        return responseDeckDto;
        return null;
    }

    @Override
    public ResponseDeckDto importDeck(DeckImportThisSiteRequestDto deckImportThisSiteRequestDto) {
//        Map<String, Integer> map = deckImportThisSiteRequestDto.getDeck();
//        ResponseDeckDto responseDeckDto = new ResponseDeckDto();
//        for (Map.Entry<String, Integer> entry : map.entrySet()) {
//            Optional<CardImgEntity> cardImg = cardImgRepository.findById(Integer.parseInt(entry.getKey()));
//            cardImg.ifPresent(cardImgEntity -> responseDeckDto.addCard(cardImgEntity, entry.getValue(), prefixUrl));
//        }
//
//
//        return responseDeckDto;
        return null;
    }
    
    @Override
    public ResponseDeckDto importDeckByDeckMap(Map<Integer, Integer> map)
    {
//        ResponseDeckDto responseDeckDto = new ResponseDeckDto();
//        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
//            Optional<CardImgEntity> cardImg = cardImgRepository.findById(entry.getKey());
//            cardImg.ifPresent(cardImgEntity -> responseDeckDto.addCard(cardImgEntity, entry.getValue(), prefixUrl));
//        }
//
//        return responseDeckDto;
        return null;
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
                    for (int i = 0; i < integerEntry.getValue(); i++) {
                        digitamas.add(cardImgEntity);
                    }
                    continue;
                }
                for (int i = 0; i < integerEntry.getValue(); i++) {
                    ttsDeckFileDto.addCard(index++, prefixUrl + cardImgEntity.getDisplayImage(), backDigimonUrl);
                }
            } catch (Exception ignore) {
            }
        }

        for (CardImgEntity digitama : digitamas) {
            try {
                ttsDeckFileDto.addCard(index++, prefixUrl + digitama.getDisplayImage(), backDigitamaUrl);
            } catch (Exception ignore) {

            }
        }

        return ttsDeckFileDto;
    }

    @Override
    @Transactional
    public PagedResponseDeckDto finMyDecks(User user, DeckSearchParameter deckSearchParameter) {
        if (user == null) {
            throw new UnAuthorizationException("");
        }
        BooleanBuilder builder = getBuilderByDeckSearchParameter(deckSearchParameter);
        
        QDeckEntity qDeckEntity = QDeckEntity.deckEntity;
        builder.and(qDeckEntity.user.eq(user));

        if (deckSearchParameter.getFormatId() != null) {
            builder.and(qDeckEntity.format.id.eq(deckSearchParameter.getFormatId()));
        }

        
        return new PagedResponseDeckDto(getDeckPage(builder, deckSearchParameter));
    }


    @Override
    public PagedResponseDeckDto findDecks(DeckSearchParameter deckSearchParameter, User user) {
        BooleanBuilder builder = getBuilderByDeckSearchParameter(deckSearchParameter);
        QDeckEntity qDeckEntity = QDeckEntity.deckEntity;
        
        if(user == null || user.getRole().equals(Role.USER)) {
            builder.and(qDeckEntity.isPublic.eq(true));    
        }

        if (deckSearchParameter.getFormatId() != null) {
            builder.and(qDeckEntity.format.id.eq(deckSearchParameter.getFormatId()));
        }

        return new PagedResponseDeckDto(getDeckPage(builder, deckSearchParameter));
    }

    private Page<DeckEntity> getDeckPage(BooleanBuilder builder, DeckSearchParameter deckSearchParameter) {
        QDeckEntity qDeckEntity = QDeckEntity.deckEntity;
        Pageable pageable = generatePageableByDeckSearchParameter(deckSearchParameter);

        long totalCount = new JPAQuery<>(entityManager)
                .select(qDeckEntity.id.count())
                .from(qDeckEntity)
                .where(builder)
                .fetchOne();

        List<Integer> deckIds = new JPAQuery<>(entityManager)
                .select(qDeckEntity.id)
                .from(qDeckEntity)
                .where(builder)
                .orderBy(qDeckEntity.updateTimestamp.desc().nullsLast())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<DeckEntity> deckEntities = deckRepository.findByIdInOrderByUpdateTimestampDesc(deckIds);

        return new PageImpl<>(deckEntities, pageable, totalCount);
    }

    @Override
    @Transactional
    public void deleteDeck(Integer deckId, User user) {
        DeckEntity deck = deckRepository.findById(deckId).orElseThrow();
        if (!deck.getUser().equals(user)) {
            throw new ForbiddenAccessException();
        }
        deckCardRepository.deleteAll(deck.getDeckCardEntities());
        deckColorRepository.deleteAll(deck.getDeckColors());
        deckRepository.delete(deck);
    }

    @Override
    public List<ResponseDeckDto> findAllMyDeck(User user) {
        List<DeckEntity> decks = deckRepository.findByUser(user);
        List<ResponseDeckDto> responseDeckDtoList = new ArrayList<>();

        for (DeckEntity deck : decks) {
            responseDeckDtoList.add(new ResponseDeckDto(deck));
        }

        return responseDeckDtoList;
    }

    private BooleanBuilder getBuilderByDeckSearchParameter(DeckSearchParameter deckSearchParameter) {
        QDeckEntity qDeckEntity = QDeckEntity.deckEntity;
        QDeckCardEntity qDeckCardEntity = QDeckCardEntity.deckCardEntity;
        BooleanBuilder builder = new BooleanBuilder();

        if (Boolean.TRUE.equals(deckSearchParameter.getIsOnlyValidDeck())) {
            builder.and(qDeckEntity.isValid.isTrue());

        }

        if (Boolean.TRUE.equals(deckSearchParameter.getIsOnlyValidDeck()) && deckSearchParameter.getLimitId() != null) {
            Optional<LimitEntity> limitEntity = limitRepository.findById(deckSearchParameter.getLimitId());
            if (limitEntity.isPresent()) {
                Set<LimitCardEntity> limitCardEntities = limitEntity.get().getLimitCardEntities();


                for (LimitCardEntity limitCardEntity : limitCardEntities) {
                    CardEntity card = limitCardEntity.getCardEntity();
                    Integer allowedQuantity = limitCardEntity.getAllowedQuantity();


                    JPQLQuery<Integer> totalCardCountQuery = JPAExpressions
                            .select(qDeckCardEntity.cnt.sum().coalesce(0))
                            .from(qDeckCardEntity)
                            .where(qDeckCardEntity.deckEntity.eq(qDeckEntity)
                                    .and(qDeckCardEntity.cardImgEntity.cardEntity.eq(card)));

                    builder.and(totalCardCountQuery.loe(allowedQuantity));

                }
            }
        }


        //검색어
        if (deckSearchParameter.getSearchString() != null && !deckSearchParameter.getSearchString().isEmpty()) {
            builder.and(
                    qDeckEntity.deckName.containsIgnoreCase(deckSearchParameter.getSearchString())
            );
        }

        //색상
        if (deckSearchParameter.getColorOperation() == 1) {
            builder.and(qDeckEntity.deckColors.size().eq(deckSearchParameter.getColors().size()));
            for (Color color : deckSearchParameter.getColors()) {
                builder.and(qDeckEntity.deckColors.any().color.eq(color));
            }
        } else {
            builder.and(qDeckEntity.deckColors.any().color.in(deckSearchParameter.getColors()));
        }



        return builder;

    }

    private Pageable generatePageableByDeckSearchParameter(DeckSearchParameter deckSearchParameter) {
        return PageRequest.of(deckSearchParameter.getPage() - 1, deckSearchParameter.getSize());
    }

    public FormatDeckCountResponseDto getFormatDeckCount(User user) {
        FormatDeckCountResponseDto response = new FormatDeckCountResponseDto();
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        QDeckEntity deck = QDeckEntity.deckEntity;
        QFormat format = QFormat.format;

        List<Tuple> allResult = queryFactory
                .select(format.id, deck.id.count())
                .from(deck)
                .join(deck.format, format)
                .where(deck.isValid.eq(true))
                .where(deck.isPublic.eq(true))
                .groupBy(format.id)
                .fetch();
        
        response.setAllFormatCount(
                allResult.stream()
                .map(tuple -> new FormatDeckCountResponseDto.FormatCount(
                        tuple.get(format.id),
                        Math.toIntExact(tuple.get(deck.id.count()))
                ))
                .collect(Collectors.toList()));
        
        if (user != null) {
            List<Tuple> myResult = queryFactory
                    .select(format.id, deck.id.count())
                    .from(deck)
                    .join(deck.format, format)
                    .where(deck.user.eq(user))
                    .groupBy(format.id)
                    .fetch();
            response.setMyFormatCount(myResult.stream()
                    .map(tuple -> new FormatDeckCountResponseDto.FormatCount(
                            tuple.get(format.id),
                            Math.toIntExact(tuple.get(deck.id.count()))
                    ))
                    .collect(Collectors.toList()));
        }


        return response;
    }
    

}
