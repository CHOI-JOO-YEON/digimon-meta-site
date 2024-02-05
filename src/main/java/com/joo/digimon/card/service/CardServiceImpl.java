package com.joo.digimon.card.service;

import com.joo.digimon.card.dto.CardRequestDto;
import com.joo.digimon.card.dto.CardResponseDto;
import com.joo.digimon.card.model.CardImgEntity;
import com.joo.digimon.card.model.QCardEntity;
import com.joo.digimon.card.model.QCardImgEntity;
import com.joo.digimon.card.repository.CardImgRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private final CardImgRepository cardImgRepository;

    @Value("${domain.url}")
    private String prefixUrl;

    @Override
    public CardResponseDto searchCards(CardRequestDto cardRequestDto) {
        QCardEntity qCardEntity = QCardEntity.cardEntity;
        QCardImgEntity qCardImgEntity = QCardImgEntity.cardImgEntity;
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

        Pageable pageable;
        Sort sort = Sort.by(Sort.Order.asc("cardEntity.cardType"));

        Sort.Order orderOptionSort;
        if (cardRequestDto.getIsOrderDesc()) {
            orderOptionSort = Sort.Order.desc("cardEntity." + cardRequestDto.getOrderOption());
        } else {
            orderOptionSort = Sort.Order.asc("cardEntity." + cardRequestDto.getOrderOption());
        }

        // 두 정렬 조건 결합
        sort = sort.and(Sort.by(orderOptionSort));


        pageable = PageRequest.of(cardRequestDto.getPage() - 1, cardRequestDto.getSize(), sort);
        Page<CardImgEntity> cardImgEntityPage = cardImgRepository.findAll(builder, pageable);


        return new CardResponseDto(cardImgEntityPage, prefixUrl);
    }

}
