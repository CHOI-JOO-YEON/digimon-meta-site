package com.joo.digimon.crawling.service;

import com.joo.digimon.crawling.dto.*;
import com.joo.digimon.global.enums.Locale;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;

public interface CrawlingService {

    int crawlAndSaveByUrl(String url, Locale locale, String note) throws IOException;
    CrawlingResultDto reCrawlingByLocale(Locale locale) throws IOException;
}
