package com.joo.digimon.limit.service;

import com.joo.digimon.limit.dto.CreateLimitRequestDto;
import com.joo.digimon.limit.dto.GetLimitResponseDto;

import java.util.List;

public interface LimitService {
    List<GetLimitResponseDto> findAll();
    void create(CreateLimitRequestDto createLimitRequestDto);
}
