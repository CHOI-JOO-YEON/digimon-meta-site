package com.joo.digimon.card.service;

import com.joo.digimon.card.dto.card.CardResponseDto;
import com.joo.digimon.card.dto.card.CardSearchRequestDto;
import com.joo.digimon.card.dto.card.CardSummeryDto;
import com.joo.digimon.card.dto.note.ResponseNoteDto;
import com.joo.digimon.card.dto.type.CardTypeResponseDto;
import com.joo.digimon.card.model.*;
import com.joo.digimon.card.repository.CardImgRepository;
import com.joo.digimon.card.repository.CardRepository;
import com.joo.digimon.card.repository.NoteRepository;
import com.joo.digimon.card.repository.TypeRepository;
import com.joo.digimon.global.enums.CardType;
import com.joo.digimon.global.enums.Form;
import com.joo.digimon.global.enums.Rarity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private final CardImgRepository cardImgRepository;
    private final CardRepository cardRepository;
    private final NoteRepository noteRepository;
    private final EntityManager entityManager;
    private final TypeRepository typeRepository;

    @Value("${domain.url}")
    private String prefixUrl;

    @Override
    public CardResponseDto searchCards(CardSearchRequestDto cardSearchRequestDto) {
        QCardImgEntity cardImg = QCardImgEntity.cardImgEntity;
        QCardEntity card = QCardEntity.cardEntity;
        QEnglishCardEntity englishCard = QEnglishCardEntity.englishCardEntity;
        QJapaneseCardEntity japaneseCard = QJapaneseCardEntity.japaneseCardEntity;
        QCardCombineTypeEntity cardCombine = QCardCombineTypeEntity.cardCombineTypeEntity;

        JPAQuery<CardImgEntity> query = new JPAQuery<>(entityManager);
        query.from(cardImg)
                .leftJoin(cardImg.cardEntity, card)
                .leftJoin(card.englishCard, englishCard)
                .leftJoin(card.japaneseCardEntity, japaneseCard);

        BooleanBuilder builder = new BooleanBuilder();

        applySearchConditions(cardSearchRequestDto, builder, cardImg, card, cardCombine, englishCard, japaneseCard);

        Page<CardImgEntity> pageableResult = getPageableResult(cardSearchRequestDto, builder, cardImg, card, englishCard, japaneseCard);
        return new CardResponseDto(pageableResult, prefixUrl);
    }

    private Page<CardImgEntity> getPageableResult(CardSearchRequestDto cardSearchRequestDto, BooleanBuilder builder, QCardImgEntity cardImg, QCardEntity card, QEnglishCardEntity englishCard, QJapaneseCardEntity japaneseCard) {
        Sort sort = createSort(cardSearchRequestDto);
        Pageable pageable = PageRequest.of(cardSearchRequestDto.getPage() - 1, cardSearchRequestDto.getSize(), sort);

        JPAQuery<Long> query = new JPAQuery<>(entityManager);
        query.from(cardImg)
                .leftJoin(cardImg.cardEntity, card)
                .leftJoin(card.englishCard, englishCard)
                .leftJoin(card.japaneseCardEntity, japaneseCard);

        int totalCount = query.select(cardImg.id)
                .where(builder)
                .fetch().size();
        List<Integer> cardIds = query.select(cardImg.id)
                .where(builder)
                .orderBy(convertSortToOrderSpecifiers(sort))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<CardImgEntity> cardImgEntities = cardImgRepository.findByIdIn(cardIds, sort);

        return new PageImpl<>(cardImgEntities, pageable, totalCount);
    }

    private Sort createSort(CardSearchRequestDto cardSearchRequestDto) {
        List<Sort.Order> orders = new ArrayList<>();
        if ("modifiedAt".equals(cardSearchRequestDto.getOrderOption())) {
            orders.add(new Sort.Order(cardSearchRequestDto.getIsOrderDesc() ? Sort.Direction.DESC : Sort.Direction.ASC, cardSearchRequestDto.getOrderOption()));
        } else {
            orders.add(new Sort.Order(cardSearchRequestDto.getIsOrderDesc() ? Sort.Direction.DESC : Sort.Direction.ASC, "cardEntity." + cardSearchRequestDto.getOrderOption()));
        }


        if (cardSearchRequestDto.getParallelOption() == 0) {
            orders.add(new Sort.Order(Sort.Direction.ASC, "isParallel"));
        }
        orders.add(new Sort.Order(Sort.Direction.ASC, "noteEntity.releaseDate"));

        return Sort.by(orders);
    }

    private void applySearchConditions(CardSearchRequestDto cardSearchRequestDto, BooleanBuilder builder, QCardImgEntity cardImg, QCardEntity card, QCardCombineTypeEntity cardCombine, QEnglishCardEntity englishCard, QJapaneseCardEntity japaneseCard) {
        addEnglishCardCondition(cardSearchRequestDto.getIsEnglishCardInclude(), builder, cardImg, card);
        addSearchStringCondition(cardSearchRequestDto.getSearchString(), builder, card, englishCard, japaneseCard);
        addColorCondition(cardSearchRequestDto, builder, card);
        addLvCondition(cardSearchRequestDto.getLvs(), builder, card);
        addCardTypeCondition(cardSearchRequestDto.getCardTypes(), builder, card);
        addPlayCostCondition(cardSearchRequestDto, builder, card);
        addDpCondition(cardSearchRequestDto, builder, card);
        addDigivolveCostCondition(cardSearchRequestDto, builder, card);
        addRarityCondition(cardSearchRequestDto.getRarities(), builder, card);
        addParallelCondition(cardSearchRequestDto.getParallelOption(), builder, cardImg);
        addNoteCondition(cardSearchRequestDto.getNoteId(), builder, cardImg);
        addTypeCondition(cardSearchRequestDto, cardCombine, builder, cardImg);
        addFormCondition(cardSearchRequestDto.getForms(), builder, card);
    }

    private void addFormCondition(Set<Form> forms, BooleanBuilder builder, QCardEntity card) {
        if (forms != null) {
            builder.and(card.form.in(forms));
        }
    }

    private void addTypeCondition(CardSearchRequestDto cardSearchRequestDto, QCardCombineTypeEntity cardCombine, BooleanBuilder builder, QCardImgEntity cardImg) {
        if (cardSearchRequestDto.getTypeIds() != null && !cardSearchRequestDto.getTypeIds().isEmpty()) {
            JPQLQuery<Integer> jpqlQuery = null;
            if (cardSearchRequestDto.getTypeOperation() == 0) {
                jpqlQuery = JPAExpressions.select(cardCombine.cardEntity.id)
                        .from(cardCombine)
                        .where(cardCombine.typeEntity.id.in(cardSearchRequestDto.getTypeIds()))
                        .groupBy(cardCombine.cardEntity.id)
                        .having(cardCombine.typeEntity.id.countDistinct().eq(Long.valueOf(cardSearchRequestDto.getTypeIds().size())));
            } else if (cardSearchRequestDto.getTypeOperation() == 1) {
                jpqlQuery = JPAExpressions.select(cardCombine.cardEntity.id)
                        .from(cardCombine)
                        .where(cardCombine.typeEntity.id.in(cardSearchRequestDto.getTypeIds()));
            }
            builder.and(cardImg.cardEntity.id.in(jpqlQuery));
        }
    }

    private void addNoteCondition(Set<Integer> noteIds, BooleanBuilder builder, QCardImgEntity cardImg) {
        if (noteIds != null) {
            builder.and(cardImg.noteEntity.id.in(noteIds));
        }
    }

    private void addParallelCondition(Integer parallelOption, BooleanBuilder builder, QCardImgEntity cardImg) {
        if (parallelOption == 1) {
            builder.and(cardImg.isParallel.eq(false));
        } else if (parallelOption == 2) {
            builder.and(cardImg.isParallel.eq(true));
        }
    }

    private void addRarityCondition(Set<Rarity> rarities, BooleanBuilder builder, QCardEntity card) {
        if (rarities != null) {
            builder.and(card.rarity.in(rarities));
        }
    }

    private void addDigivolveCostCondition(CardSearchRequestDto cardSearchRequestDto, BooleanBuilder builder, QCardEntity card) {
        if (cardSearchRequestDto.getMinDigivolutionCost() == 0 && cardSearchRequestDto.getMaxDigivolutionCost() == 8) {
            return;
        }
        builder.and(card.digivolveCost1.between(
                cardSearchRequestDto.getMinDigivolutionCost(),
                cardSearchRequestDto.getMaxDigivolutionCost()).or(card.digivolveCost2.between(
                cardSearchRequestDto.getMinDigivolutionCost(),
                cardSearchRequestDto.getMaxDigivolutionCost())
        ));
    }

    private void addDpCondition(CardSearchRequestDto cardSearchRequestDto, BooleanBuilder builder, QCardEntity card) {
        if (cardSearchRequestDto.getMinDp() == 1000 && cardSearchRequestDto.getMaxDp() == 17000) {
            return;
        }
        builder.and(card.dp.between(cardSearchRequestDto.getMinDp(), cardSearchRequestDto.getMaxDp()));
    }

    private void addPlayCostCondition(CardSearchRequestDto cardSearchRequestDto, BooleanBuilder builder, QCardEntity card) {
        if (cardSearchRequestDto.getMinPlayCost() == 0 && cardSearchRequestDto.getMaxPlayCost() == 20) {
            return;
        }
        builder.and(card.playCost.between(cardSearchRequestDto.getMinPlayCost(), cardSearchRequestDto.getMaxPlayCost()));

    }

    private void addCardTypeCondition(Set<CardType> cardTypes, BooleanBuilder builder, QCardEntity card) {
        if (cardTypes != null) {
            builder.and(card.cardType.in(cardTypes));
        }
    }

    private void addLvCondition(Set<Integer> lvs, BooleanBuilder builder, QCardEntity card) {
        if (lvs != null) {
            builder.and(card.lv.in(lvs));
        }
    }

    private void addColorCondition(CardSearchRequestDto cardSearchRequestDto, BooleanBuilder builder, QCardEntity card) {
        if (cardSearchRequestDto.getColors() == null) {
            return;
        }

        if (cardSearchRequestDto.getColorOperation() == 0) {
            builder.and(card.color1.in(cardSearchRequestDto.getColors()).and(card.color2.in(cardSearchRequestDto.getColors())).and(card.color3.in(cardSearchRequestDto.getColors())));
        } else if (cardSearchRequestDto.getColorOperation() == 1) {
            builder.and(card.color1.in(cardSearchRequestDto.getColors()).or(card.color2.in(cardSearchRequestDto.getColors())).or(card.color3.in(cardSearchRequestDto.getColors())));
        }

    }

    private void addSearchStringCondition(String searchString, BooleanBuilder builder, QCardEntity card, QEnglishCardEntity englishCard, QJapaneseCardEntity japaneseCard) {
        if (searchString != null && !searchString.isEmpty()) {
            builder.and(
                    card.cardName.containsIgnoreCase(searchString)
                            .or(card.cardNo.containsIgnoreCase(searchString))
                            .or(card.effect.containsIgnoreCase(searchString))
                            .or(card.sourceEffect.containsIgnoreCase(searchString))
                            .or(englishCard.cardName.containsIgnoreCase(searchString))
                            .or(englishCard.effect.containsIgnoreCase(searchString))
                            .or(englishCard.sourceEffect.containsIgnoreCase(searchString))
                            .or(japaneseCard.cardName.containsIgnoreCase(searchString))
                            .or(japaneseCard.effect.containsIgnoreCase(searchString))
                            .or(japaneseCard.sourceEffect.containsIgnoreCase(searchString))
            );
        }
    }

    private void addEnglishCardCondition(boolean isEnglishCardInclude, BooleanBuilder builder, QCardImgEntity cardImg, QCardEntity card) {
        if (!isEnglishCardInclude) {
            builder.and(
                    card.isOnlyEnCard.isNull()
                            .or(card.isOnlyEnCard.isFalse()));
        }
    }

    private OrderSpecifier<?>[] convertSortToOrderSpecifiers(Sort sort) {
        return sort.stream()
                .map(this::convertOrderToOrderSpecifier)
                .toArray(OrderSpecifier[]::new);
    }

    private OrderSpecifier<?> convertOrderToOrderSpecifier(Sort.Order order) {
        PathBuilder<CardImgEntity> entityPath = new PathBuilder<>(CardImgEntity.class, "cardImgEntity");

        if (order.isAscending()) {
            return new OrderSpecifier(Order.ASC, entityPath.get(order.getProperty()));
        } else {
            return new OrderSpecifier(Order.DESC, entityPath.get(order.getProperty()));
        }
    }

    @Override
    public List<ResponseNoteDto> getNotes() {
        List<ResponseNoteDto> noteDtoList = new ArrayList<>();
        List<NoteEntity> noteEntityList = noteRepository.findByIsDisableFalseOrIsDisableNullOrderByReleaseDateAscPriorityAsc();
        for (NoteEntity noteEntity : noteEntityList) {
            noteDtoList.add(new ResponseNoteDto(noteEntity));
        }

        return noteDtoList;
    }

    @Override
    public List<CardTypeResponseDto> getTypes() {
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        List<TypeEntity> typeEntities = typeRepository.findByNameIsNotNull(sort);
        List<CardTypeResponseDto> cardTypeResponseDtoList = new ArrayList<>();
        for (TypeEntity typeEntity : typeEntities) {
            cardTypeResponseDtoList.add(new CardTypeResponseDto(typeEntity));
        }
        return cardTypeResponseDtoList;
    }

    @Override
    public List<CardSummeryDto> getAllCardSummery() {
        List<CardEntity> cardEntities = cardRepository.findAll();
        List<CardSummeryDto> cardSummeryDtoList = new ArrayList<>();
        for (CardEntity cardEntity : cardEntities) {
            cardSummeryDtoList.add(new CardSummeryDto(cardEntity));
        }
        return cardSummeryDtoList;

    }
}
