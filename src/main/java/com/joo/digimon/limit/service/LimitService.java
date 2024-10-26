package com.joo.digimon.limit.service;

import com.joo.digimon.limit.dto.LimitPutRequestDto;
import com.joo.digimon.limit.dto.GetLimitResponseDto;
import jakarta.transaction.Transactional;

import java.util.List;

public interface LimitService {
    List<GetLimitResponseDto> findAll();
    void createLimit(LimitPutRequestDto limitPutRequestDto);


    @Transactional
    void updateLimits(List<LimitPutRequestDto> limitPutRequestDto);

    @Transactional
    void updateLimit(LimitPutRequestDto limitPutRequestDto);

    void deleteLimit(Integer limitId);
}
