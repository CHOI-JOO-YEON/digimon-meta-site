package com.joo.digimon.deck.service;

import com.joo.digimon.deck.dto.FormatRequestDto;
import com.joo.digimon.deck.dto.FormatResponseDto;
import com.joo.digimon.deck.dto.FormatUpdateRequestDto;

import java.time.LocalDate;
import java.util.List;

public interface FormatService {
    void createFormat(FormatRequestDto formatRequestDto);

    List<FormatResponseDto> getFormatList(LocalDate latestReleaseCardDate);


    List<FormatResponseDto> getAllFormat();

    void updateFormat(List<FormatUpdateRequestDto> dtos);

    FormatResponseDto getCurrentFormat();

    void deleteFormat(Integer formatId);

}
