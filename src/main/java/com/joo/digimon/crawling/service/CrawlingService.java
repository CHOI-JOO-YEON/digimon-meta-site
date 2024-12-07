package com.joo.digimon.crawling.service;

import com.joo.digimon.global.enums.Locale;
import java.io.IOException;

public interface CrawlingService {
    int crawlAndSaveByUrl(String url, Locale locale, String note) throws IOException;
    int setAttribute() throws IOException;
}
