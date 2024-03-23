package com.joo.digimon.crawling.service;

import com.joo.digimon.crawling.dto.CrawlingResultDto;

import java.io.IOException;

public interface CrawlingEnService {
    CrawlingResultDto crawlAndSaveByUrl(String url) throws IOException;
}
