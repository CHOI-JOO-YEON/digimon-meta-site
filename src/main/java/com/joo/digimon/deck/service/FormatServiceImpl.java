package com.joo.digimon.deck.service;

import com.joo.digimon.deck.dto.FormatRequestDto;
import com.joo.digimon.deck.dto.FormatResponseDto;
import com.joo.digimon.deck.dto.FormatUpdateRequestDto;
import com.joo.digimon.deck.model.Format;
import com.joo.digimon.deck.repository.FormatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FormatServiceImpl implements FormatService {

    private final FormatRepository formatRepository;

    @Override
    @Transactional
    public void createFormat(FormatRequestDto formatRequestDto) {
        formatRepository.save(
                Format.builder()
                        .name(formatRequestDto.getFormatName())
                        .startDate(formatRequestDto.getStartDate())
                        .endDate(formatRequestDto.getEndDate())
                        .build()
        );
    }

    @Override
    public List<FormatResponseDto> getFormatList(LocalDate latestReleaseCardDate) {
        List<FormatResponseDto> result = new ArrayList<>();

        Sort sort = Sort.by("startDate").descending();
        List<Format> formats = formatRepository.findByEndDateIsAfter(latestReleaseCardDate, sort);

        for (Format format : formats) {
            result.add(new FormatResponseDto(format));
        }
        return result;
    }

    @Override
    public void updateFormat(FormatUpdateRequestDto formatUpdateRequestDto) {

    }

    @Override
    public FormatResponseDto getCurrentFormat() {
        Format format = formatRepository.findTopByIsOnlyEnIsNullOrIsOnlyEnIsFalseOrderByStartDateDesc().orElseThrow();
        return new FormatResponseDto(format);
    }
}
