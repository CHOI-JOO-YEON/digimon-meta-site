package com.joo.digimon.crawling.service;

import com.joo.digimon.crawling.dto.CrawlingResultDto;
import com.joo.digimon.crawling.dto.UpdateCrawlingRequestDto;
import jakarta.transaction.Transactional;

import java.io.IOException;
import java.util.List;

public interface CrawlingEnService {
    @Transactional
    CrawlingResultDto updateCrawlingEntityAndSaveCard(List<UpdateCrawlingRequestDto> updateCrawlingRequestDtoList);

    CrawlingResultDto crawlAndSaveByUrl(String url) throws IOException;
    CrawlingResultDto crawlAndSaveByUrl(String url, String note) throws IOException;
}
