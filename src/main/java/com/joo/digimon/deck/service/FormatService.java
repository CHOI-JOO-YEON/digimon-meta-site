package com.joo.digimon.deck.service;

import com.joo.digimon.deck.dto.FormatRequestDto;
import com.joo.digimon.deck.dto.FormatResponseDto;
import com.joo.digimon.deck.dto.FormatUpdateRequestDto;

import java.util.List;

public interface FormatService {
    void createFormat(FormatRequestDto formatRequestDto);

    List<FormatResponseDto> getFormatList();

    void updateFormat(FormatUpdateRequestDto formatUpdateRequestDto);
}
