package com.joo.digimon.card.service;

import com.joo.digimon.card.dto.CardRequestDto;
import com.joo.digimon.card.dto.CardResponseDto;
import com.joo.digimon.card.dto.CardTypeResponseDto;
import com.joo.digimon.card.dto.ResponseNoteDto;
import com.joo.digimon.card.model.*;
import com.joo.digimon.card.repository.CardImgRepository;
import com.joo.digimon.card.repository.NoteRepository;
import com.joo.digimon.card.repository.TypeRepository;
import com.joo.digimon.global.enums.CardType;
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
    private final NoteRepository noteRepository;
    private final EntityManager entityManager;
    private final TypeRepository typeRepository;

    @Value("${domain.url}")
    private String prefixUrl;

    @Override
    public CardResponseDto searchCards(CardRequestDto cardRequestDto) {
        QCardImgEntity cardImg = QCardImgEntity.cardImgEntity;
        QCardEntity card = QCardEntity.cardEntity;
        QCardCombineTypeEntity cardCombine = QCardCombineTypeEntity.cardCombineTypeEntity;
        BooleanBuilder builder = new BooleanBuilder();

        applySearchConditions(cardRequestDto, builder, cardImg, card, cardCombine);
        Page<CardImgEntity> pageableResult = getPageableResult(cardRequestDto, builder, cardImg);
        return new CardResponseDto(pageableResult, prefixUrl);

    }

    private Page<CardImgEntity> getPageableResult(CardRequestDto cardRequestDto, BooleanBuilder builder, QCardImgEntity cardImg) {
        Sort sort = createSort(cardRequestDto);
        Pageable pageable = PageRequest.of(cardRequestDto.getPage() - 1, cardRequestDto.getSize(), sort);

        JPAQuery<Long> query = new JPAQuery<>(entityManager);
        int totalCount = query.select(cardImg.id)
                .from(cardImg)
                .where(builder)
                .fetch().size();
        List<Integer> cardIds = query.select(cardImg.id)
                .from(cardImg)
                .where(builder)
                .orderBy(convertSortToOrderSpecifiers(sort))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<CardImgEntity> cardImgEntities = cardImgRepository.findByIdIn(cardIds, sort);

        return new PageImpl<>(cardImgEntities, pageable, totalCount);
    }

    private Sort createSort(CardRequestDto cardRequestDto) {
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(cardRequestDto.getIsOrderDesc() ? Sort.Direction.DESC : Sort.Direction.ASC, "cardEntity." + cardRequestDto.getOrderOption()));

        if (cardRequestDto.getParallelOption() == 0) {
            orders.add(new Sort.Order(Sort.Direction.ASC, "isParallel"));
        }
        orders.add(new Sort.Order(Sort.Direction.ASC, "noteEntity.releaseDate"));

        return Sort.by(orders);
    }

    private void applySearchConditions(CardRequestDto cardRequestDto, BooleanBuilder builder, QCardImgEntity cardImg, QCardEntity card, QCardCombineTypeEntity cardCombine) {
        addEnglishCardCondition(cardRequestDto.getIsEnglishCardInclude(), builder, cardImg, card);
        addSearchStringCondition(cardRequestDto.getSearchString(), builder, card);
        addColorCondition(cardRequestDto, builder, card);
        addLvCondition(cardRequestDto.getLvs(), builder, card);
        addCardTypeCondition(cardRequestDto.getCardTypes(), builder, card);
        addPlayCostCondition(cardRequestDto, builder, card);
        addDpCondition(cardRequestDto, builder, card);
        addDigivolveCostCondition(cardRequestDto, builder, card);
        addRarityCondition(cardRequestDto.getRarities(), builder, card);
        addParallelCondition(cardRequestDto.getParallelOption(), builder, cardImg);
        addNoteCondition(cardRequestDto.getNoteId(), builder, cardImg);
        addTypeCondition(cardRequestDto, cardCombine, builder, cardImg);
    }

    private void addTypeCondition(CardRequestDto cardRequestDto, QCardCombineTypeEntity cardCombine, BooleanBuilder builder, QCardImgEntity cardImg) {
        if (cardRequestDto.getTypeIds() != null && !cardRequestDto.getTypeIds().isEmpty()) {
            JPQLQuery<Integer> jpqlQuery = null;
            if (cardRequestDto.getTypeOperation() == 0) {
                jpqlQuery = JPAExpressions.select(cardCombine.cardEntity.id)
                        .from(cardCombine)
                        .where(cardCombine.typeEntity.id.in(cardRequestDto.getTypeIds()))
                        .groupBy(cardCombine.cardEntity.id)
                        .having(cardCombine.typeEntity.id.countDistinct().eq(Long.valueOf(cardRequestDto.getTypeIds().size())));
            } else if (cardRequestDto.getTypeOperation() == 1) {
                jpqlQuery = JPAExpressions.select(cardCombine.cardEntity.id)
                        .from(cardCombine)
                        .where(cardCombine.typeEntity.id.in(cardRequestDto.getTypeIds()));
            }
            builder.and(cardImg.cardEntity.id.in(jpqlQuery));
        }
    }

    private void addNoteCondition(Integer noteId, BooleanBuilder builder, QCardImgEntity cardImg) {
        if (noteId != null) {
            builder.and(cardImg.noteEntity.id.eq(noteId));
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

    private void addDigivolveCostCondition(CardRequestDto cardRequestDto, BooleanBuilder builder, QCardEntity card) {
        if(cardRequestDto.getMinDigivolutionCost()==0&&cardRequestDto.getMaxDigivolutionCost() == 8)
        {
            return;
        }
        builder.and(card.digivolveCost1.between(
                cardRequestDto.getMinDigivolutionCost(),
                cardRequestDto.getMaxDigivolutionCost()).or(card.digivolveCost2.between(
                cardRequestDto.getMinDigivolutionCost(),
                cardRequestDto.getMaxDigivolutionCost())
        ));
    }

    private void addDpCondition(CardRequestDto cardRequestDto, BooleanBuilder builder, QCardEntity card) {
        if (cardRequestDto.getMinDp() == 1000 && cardRequestDto.getMaxDp() == 17000) {
            return;
        }
        builder.and(card.dp.between(cardRequestDto.getMinDp(), cardRequestDto.getMaxDp()));
    }

    private void addPlayCostCondition(CardRequestDto cardRequestDto, BooleanBuilder builder, QCardEntity card) {
        if (cardRequestDto.getMinPlayCost() == 0 && cardRequestDto.getMaxPlayCost() == 20) {
            return;
        }
        builder.and(card.playCost.between(cardRequestDto.getMinPlayCost(), cardRequestDto.getMaxPlayCost()));

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

    private void addColorCondition(CardRequestDto cardRequestDto, BooleanBuilder builder, QCardEntity card) {
        if (cardRequestDto.getColors() == null) {
            return;
        }

        if (cardRequestDto.getColorOperation() == 0) {
            builder.and(card.color1.in(cardRequestDto.getColors()).and(card.color2.in(cardRequestDto.getColors())).and(card.color3.in(cardRequestDto.getColors())));
        } else if (cardRequestDto.getColorOperation() == 1) {
            builder.and(card.color1.in(cardRequestDto.getColors()).or(card.color2.in(cardRequestDto.getColors())).or(card.color3.in(cardRequestDto.getColors())));
        }

    }

    private void addSearchStringCondition(String searchString, BooleanBuilder builder, QCardEntity card) {
        if (searchString != null && !searchString.isEmpty()) {
            builder.and(
                    card.cardName.containsIgnoreCase(searchString)
                            .or(card.cardNo.containsIgnoreCase(searchString))
                            .or(card.effect.containsIgnoreCase(searchString))
                            .or(card.sourceEffect.containsIgnoreCase(searchString))
            );
        }
    }

    private void addEnglishCardCondition(boolean isEnglishCardInclude, BooleanBuilder builder, QCardImgEntity cardImg, QCardEntity card) {
        if (!isEnglishCardInclude) {
            builder.and(cardImg.isEnCard.isNull()).or(cardImg.isEnCard.isFalse());
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
        Sort sort = Sort.by(Sort.Direction.ASC,"name");
        List<TypeEntity> typeEntities = typeRepository.findAll(sort);
        List<CardTypeResponseDto> cardTypeResponseDtoList = new ArrayList<>();
        for (TypeEntity typeEntity : typeEntities) {
            cardTypeResponseDtoList.add(new CardTypeResponseDto(typeEntity));
        }
        return cardTypeResponseDtoList;
    }

}
