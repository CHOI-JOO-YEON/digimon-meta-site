package com.joo.digimon.card.service;

import com.joo.digimon.card.dto.CardRequestDto;
import com.joo.digimon.card.dto.CardResponseDto;
import com.joo.digimon.card.dto.NoteDto;
import com.joo.digimon.card.model.*;
import com.joo.digimon.card.repository.CardImgRepository;
import com.joo.digimon.card.repository.NoteRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private final CardImgRepository cardImgRepository;
    private final NoteRepository noteRepository;
    private final EntityManager entityManager;

    @Value("${domain.url}")
    private String prefixUrl;

    @Override
    public CardResponseDto searchCards(CardRequestDto cardRequestDto) {
        QCardImgEntity qCardImgEntity = QCardImgEntity.cardImgEntity;
        QCardEntity qCardEntity = QCardEntity.cardEntity;
        QCardCombineTypeEntity qCardCombineTypeEntity = QCardCombineTypeEntity.cardCombineTypeEntity;
        QTypeEntity qTypeEntity = QTypeEntity.typeEntity;
        BooleanBuilder builder = new BooleanBuilder();

        //검색어
        if (cardRequestDto.getSearchString() != null && !cardRequestDto.getSearchString().isEmpty()) {
            builder.and(
                    qCardEntity.cardName.containsIgnoreCase(cardRequestDto.getSearchString())
                            .or(qCardEntity.cardNo.containsIgnoreCase(cardRequestDto.getSearchString()))
                            .or(qCardEntity.effect.containsIgnoreCase(cardRequestDto.getSearchString()))
                            .or(qCardEntity.sourceEffect.containsIgnoreCase(cardRequestDto.getSearchString()))
            );
        }

        //색상
        if (cardRequestDto.getColors() != null) {
            if (cardRequestDto.getColorOperation() == 0) {
                builder.and(qCardEntity.color1.in(cardRequestDto.getColors()).and(qCardEntity.color2.in(cardRequestDto.getColors())));
            } else if (cardRequestDto.getColorOperation() == 1) {
                builder.and(qCardEntity.color1.in(cardRequestDto.getColors()).or(qCardEntity.color2.in(cardRequestDto.getColors())));
            }
        }

        //lv
        if (cardRequestDto.getLvs() != null) {
            builder.and(qCardEntity.lv.in(cardRequestDto.getLvs()));
        }

        //카드 타입
        if (cardRequestDto.getCardTypes() != null) {
            builder.and(qCardEntity.cardType.in(cardRequestDto.getCardTypes()));
        }

        //playCost
        if (cardRequestDto.getMinPlayCost() != 0) {
            if (cardRequestDto.getMaxPlayCost() != 20) {
                builder.and(qCardEntity.playCost.between(cardRequestDto.getMinPlayCost(), cardRequestDto.getMaxPlayCost()));
            } else {
                builder.and(qCardEntity.playCost.goe(cardRequestDto.getMinPlayCost()));
            }
        } else {
            if (cardRequestDto.getMaxPlayCost() != 20) {
                builder.and(qCardEntity.playCost.loe(cardRequestDto.getMaxPlayCost()));
            }
        }

        //dp
        if (cardRequestDto.getMinDp() != 1000) {
            if (cardRequestDto.getMaxDp() != 16000) {
                builder.and(qCardEntity.dp.between(cardRequestDto.getMinDp(), cardRequestDto.getMaxDp()));
            } else {
                builder.and(qCardEntity.dp.goe(cardRequestDto.getMinDp()));
            }
        } else {
            if (cardRequestDto.getMaxDp() != 16000) {
                builder.and(qCardEntity.dp.loe(cardRequestDto.getMaxDp()));
            }
        }

        //진화코스트
        if (cardRequestDto.getMinDigivolutionCost() != 0) {
            if (cardRequestDto.getMaxDigivolutionCost() != 8) {
                builder.and(qCardEntity.digivolveCost1.between(
                        cardRequestDto.getMinDigivolutionCost(),
                        cardRequestDto.getMaxDigivolutionCost()).or(qCardEntity.digivolveCost2.between(
                        cardRequestDto.getMinDigivolutionCost(),
                        cardRequestDto.getMaxDigivolutionCost())
                ));
            } else {
                builder.and(qCardEntity.digivolveCost1.goe(
                        cardRequestDto.getMinDigivolutionCost()).or(qCardEntity.digivolveCost2.goe(
                        cardRequestDto.getMinDigivolutionCost())
                ));
            }
        } else {
            if (cardRequestDto.getMaxDigivolutionCost() != 8) {
                builder.and(qCardEntity.digivolveCost1.loe(
                        cardRequestDto.getMaxDigivolutionCost()).or(qCardEntity.digivolveCost2.loe(
                        cardRequestDto.getMaxDigivolutionCost())
                ));
            }
        }

        //레어도
        if (cardRequestDto.getRarities() != null) {
            builder.and(qCardEntity.rarity.in(cardRequestDto.getRarities()));
        }
        if (cardRequestDto.getParallelOption() == 1) {
            builder.and(qCardImgEntity.isParallel.eq(false));
        } else if (cardRequestDto.getParallelOption() == 2) {
            builder.and(qCardImgEntity.isParallel.eq(true));
        }
        if (cardRequestDto.getNoteId() != null) {
            builder.and(qCardImgEntity.noteEntity.id.eq(cardRequestDto.getNoteId()));
        }


        Pageable pageable;

        Sort.Order orderOptionSort;
        if (cardRequestDto.getIsOrderDesc()) {
            orderOptionSort = Sort.Order.desc("cardEntity." + cardRequestDto.getOrderOption());
        } else {
            orderOptionSort = Sort.Order.asc("cardEntity." + cardRequestDto.getOrderOption());
        }

        Sort sort = Sort.by(orderOptionSort);


        pageable = PageRequest.of(cardRequestDto.getPage() - 1, cardRequestDto.getSize(), sort);

        JPAQuery<Long> query = new JPAQuery<>(entityManager);
        int totalCount = query.select(qCardImgEntity.id)
                .from(qCardImgEntity)
                .where(builder)
                .fetch().size();
        List<Integer> cardIds = query.select(qCardImgEntity.id)
                .from(qCardImgEntity)
                .where(builder)
                .orderBy(getOrderSpecifiers(sort))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();



        List<CardImgEntity> cardImgEntities = cardImgRepository.findByIdIn(cardIds,sort);
        int totalPages = (int) Math.ceil((double) totalCount / cardRequestDto.getSize());

        return new CardResponseDto(cardImgEntities, prefixUrl, cardRequestDto.getPage() - 1, totalCount, totalPages);
    }
    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        return sort.stream()
                .map(this::toOrderSpecifier)
                .toArray(OrderSpecifier[]::new);
    }

    private OrderSpecifier<?> toOrderSpecifier(Sort.Order order) {
        PathBuilder<CardImgEntity> entityPath = new PathBuilder<>(CardImgEntity.class, "cardImgEntity");

        if (order.isAscending()) {
            return new OrderSpecifier(Order.ASC, entityPath.get(order.getProperty()));
        } else {
            return new OrderSpecifier(Order.DESC, entityPath.get(order.getProperty()));
        }
    }
    @Override
    public List<NoteDto> getNotes() {
        List<NoteDto> noteDtoList = new ArrayList<>();
        List<NoteEntity> noteEntityList = noteRepository.findAllByOrderByReleaseDate();
        for (NoteEntity noteEntity : noteEntityList) {
            noteDtoList.add(new NoteDto(noteEntity));
        }

        return noteDtoList;
    }

}
