package com.joo.digimon.limit.service;

import com.joo.digimon.card.model.CardEntity;
import com.joo.digimon.card.repository.CardRepository;
import com.joo.digimon.limit.dto.LimitPair;
import com.joo.digimon.limit.dto.LimitPutRequestDto;
import com.joo.digimon.limit.dto.GetLimitResponseDto;
import com.joo.digimon.limit.model.LimitCardEntity;
import com.joo.digimon.limit.model.LimitEntity;
import com.joo.digimon.limit.model.LimitPairCardEntity;
import com.joo.digimon.limit.model.LimitPairEntity;
import com.joo.digimon.limit.repository.LimitCardRepository;
import com.joo.digimon.limit.repository.LimitPairCardRepository;
import com.joo.digimon.limit.repository.LimitPairRepository;
import com.joo.digimon.limit.repository.LimitRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class LimitServiceImpl implements LimitService {
    private final LimitRepository limitRepository;
    private final LimitCardRepository limitCardRepository;
    private final LimitPairRepository limitPairRepository;
    private final LimitPairCardRepository limitPairCardRepository;
    private final CardRepository cardRepository;

    @PostConstruct
    @Transactional
    public void init() {
        List<LimitEntity> limitEntities = limitRepository.findAll();
        if (!limitEntities.isEmpty()) {
            return;
        }
        limitRepository.save(LimitEntity.builder()
                .restrictionBeginDate(LocalDate.now())
                .build());
    }

    @Override
    @Transactional
    public List<GetLimitResponseDto> findAll() {
        List<LimitEntity> limitEntities = limitRepository.findAll();

        limitEntities.sort((o1, o2) -> o2.getRestrictionBeginDate().compareTo(o1.getRestrictionBeginDate()));

        List<GetLimitResponseDto> limitResponseDtoList = new ArrayList<>();
        for (LimitEntity limitEntity : limitEntities) {
            limitResponseDtoList.add(new GetLimitResponseDto(limitEntity));
        }
        return limitResponseDtoList;
    }

    @Override
    @Transactional
    public void createLimit(LimitPutRequestDto limitPutRequestDto) {
        LimitEntity limitEntity = limitRepository.save(LimitEntity.builder()
                .restrictionBeginDate(limitPutRequestDto.getRestrictionBeginDate())
                .build());

        updateLimitCardEntity(limitPutRequestDto, limitEntity);
        updateLimitPairEntity(limitPutRequestDto.getLimitPairList(), limitEntity);

    }

    @Transactional
    public void updateLimitPairEntity(List<LimitPair> limitPairRequestDtoList, LimitEntity limitEntity) {
        deleteLimitPairEntity(limitEntity);
        if (limitPairRequestDtoList == null || limitPairRequestDtoList.isEmpty()) {
            return;
        }

        Set<String> allCardNos = limitPairRequestDtoList.stream()
                .flatMap(dto -> Stream.concat(dto.getACardPairNos().stream(), dto.getBCardPairNos().stream()))
                .collect(Collectors.toSet());

        Map<String, CardEntity> cardEntityMap = cardRepository.findByCardNoIn(allCardNos).stream()
                .collect(Collectors.toMap(CardEntity::getCardNo, Function.identity()));

        List<LimitPairEntity> limitPairEntities = limitPairRequestDtoList.stream()
                .map(limitPair -> {
                    LimitPairEntity limitPairEntity = LimitPairEntity.builder()
                            .limitEntity(limitEntity)
                            .pairACardSet(new HashSet<>()) 
                            .pairBCardSet(new HashSet<>()) 
                            .build();

                    Set<LimitPairCardEntity> pairACards = limitPair.getACardPairNos().stream()
                            .map(cardEntityMap::get)
                            .filter(Objects::nonNull)
                            .map(card -> LimitPairCardEntity.builder()
                                    .cardEntity(card)
                                    .pairA(limitPairEntity) // 연관 관계 설정
                                    .build())
                            .collect(Collectors.toSet());

                    Set<LimitPairCardEntity> pairBCards = limitPair.getBCardPairNos().stream()
                            .map(cardEntityMap::get)
                            .filter(Objects::nonNull)
                            .map(card -> LimitPairCardEntity.builder()
                                    .cardEntity(card)
                                    .pairB(limitPairEntity) // 연관 관계 설정
                                    .build())
                            .collect(Collectors.toSet());

                    limitPairEntity.getPairACardSet().addAll(pairACards);
                    limitPairEntity.getPairBCardSet().addAll(pairBCards);

                    limitPairCardRepository.saveAll(pairACards);
                    limitPairCardRepository.saveAll(pairBCards);

                    return limitPairEntity;
                })
                .collect(Collectors.toList());

        limitPairRepository.saveAll(limitPairEntities);
    }

    @Transactional
    @Override
    public void updateLimits(List<LimitPutRequestDto> limitPutRequestDto) {
        for (LimitPutRequestDto putRequestDto : limitPutRequestDto) {
            updateLimit(putRequestDto);
        }
    }

    @Override
    @Transactional
    public void updateLimit(LimitPutRequestDto limitPutRequestDto) {
        LimitEntity limitEntity = limitRepository.findById(limitPutRequestDto.getId()).orElseThrow();
        limitEntity.updateBeginDate(limitPutRequestDto.getRestrictionBeginDate());
        updateLimitCardEntity(limitPutRequestDto, limitEntity);
        updateLimitPairEntity(limitPutRequestDto.getLimitPairList(), limitEntity);
    }

    @Override
    @Transactional
    public void deleteLimit(Integer limitId) {
        LimitEntity limitEntity = limitRepository.findById(limitId).orElseThrow();
        deleteLimitCardEntity(limitEntity);
        limitRepository.delete(limitEntity);
    }

    @Transactional
    public void updateLimitCardEntity(LimitPutRequestDto limitPutRequestDto, LimitEntity limitEntity) {
        deleteLimitCardEntity(limitEntity);
        List<LimitCardEntity> limitCardEntities = new ArrayList<>();

        if (limitPutRequestDto.getBanList() != null) {
            for (String cardNo : limitPutRequestDto.getBanList()) {
                CardEntity cardEntity = cardRepository.findByCardNo(cardNo).orElseThrow();
                limitCardEntities.add(LimitCardEntity.builder()
                        .allowedQuantity(0)
                        .cardEntity(cardEntity)
                        .limitEntity(limitEntity)
                        .build());
            }
        }
        if (limitPutRequestDto.getRestrictList() != null) {
            for (String cardNo : limitPutRequestDto.getRestrictList()) {
                CardEntity cardEntity = cardRepository.findByCardNo(cardNo).orElseThrow();
                limitCardEntities.add(LimitCardEntity.builder()
                        .allowedQuantity(1)
                        .cardEntity(cardEntity)
                        .limitEntity(limitEntity)
                        .build());
            }
        }
        limitCardRepository.saveAll(limitCardEntities);
    }

    @Transactional
    public void deleteLimitCardEntity(LimitEntity limitEntity) {
        if (limitEntity.getLimitCardEntities() != null && !limitEntity.getLimitCardEntities().isEmpty()) {
            limitCardRepository.deleteAll(limitEntity.getLimitCardEntities());
        }
    }

    @Transactional
    public void deleteLimitPairEntity(LimitEntity limitEntity) {
        if (limitEntity.getLimitPairEntities() != null && !limitEntity.getLimitPairEntities().isEmpty()) {
            limitPairRepository.deleteAll(limitEntity.getLimitPairEntities());
        }
    }


}
