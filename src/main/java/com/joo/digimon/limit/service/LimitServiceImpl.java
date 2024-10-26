package com.joo.digimon.limit.service;

import com.joo.digimon.card.model.CardEntity;
import com.joo.digimon.card.repository.CardRepository;
import com.joo.digimon.limit.dto.LimitPutRequestDto;
import com.joo.digimon.limit.dto.GetLimitResponseDto;
import com.joo.digimon.limit.model.LimitCardEntity;
import com.joo.digimon.limit.model.LimitEntity;
import com.joo.digimon.limit.repository.LimitCardRepository;
import com.joo.digimon.limit.repository.LimitRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LimitServiceImpl implements LimitService {
    private final LimitRepository limitRepository;
    private final LimitCardRepository limitCardRepository;
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
        limitEntity.update(limitPutRequestDto);
        updateLimitCardEntity(limitPutRequestDto, limitEntity);
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
            limitCardRepository.deleteAll();
        }
    }


}
