package com.joo.digimon.crawling.service;

import com.joo.digimon.crawling.dto.ReflectCardRequestDto;
import com.joo.digimon.crawling.model.CrawlingCardEntity;
import com.joo.digimon.exception.model.CardParseException;

public interface CardParseService {
    ReflectCardRequestDto crawlingCardParse(CrawlingCardEntity crawlingCardEntity) throws CardParseException;
}
