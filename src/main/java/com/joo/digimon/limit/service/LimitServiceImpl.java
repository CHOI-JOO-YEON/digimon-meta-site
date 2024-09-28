package com.joo.digimon.limit.service;

import com.joo.digimon.card.model.CardEntity;
import com.joo.digimon.card.repository.CardRepository;
import com.joo.digimon.limit.dto.CreateLimitRequestDto;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LimitServiceImpl implements LimitService {
    private final LimitRepository limitRepository;
    private final LimitCardRepository limitCardRepository;
    private final CardRepository cardRepository;

    @PostConstruct
    @Transactional
    public void init() {
        Optional<LimitEntity> limitEntity = limitRepository.findById(1);
        if (limitEntity.isPresent()) {
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
    public void create(CreateLimitRequestDto createLimitRequestDto) {
        LimitEntity limitEntity = limitRepository.save(LimitEntity.builder()
                .restrictionBeginDate(createLimitRequestDto.getRestrictionBeginDate())
                .build());

        List<LimitCardEntity> limitCardEntities = new ArrayList<>();

        if (createLimitRequestDto.getBanList() != null) {
            for (String cardNo : createLimitRequestDto.getBanList()) {
                CardEntity cardEntity = cardRepository.findByCardNo(cardNo).orElseThrow();
                limitCardEntities.add(LimitCardEntity.builder()
                        .allowedQuantity(0)
                        .cardEntity(cardEntity)
                        .limitEntity(limitEntity)
                        .build());
            }
        }
        if (createLimitRequestDto.getRestrictList() != null) {
            for (String cardNo : createLimitRequestDto.getRestrictList()) {
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
}
