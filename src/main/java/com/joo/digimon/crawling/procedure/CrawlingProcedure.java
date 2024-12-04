package com.joo.digimon.crawling.procedure;

import com.joo.digimon.crawling.dto.CrawlingCardDto;
import org.jsoup.select.Elements;

public interface CrawlingProcedure {
    static String parseElementToPlainText(Elements select) {
        return select.html().replace("<br>\n", "");
    }
    CrawlingCardDto crawl();
}
