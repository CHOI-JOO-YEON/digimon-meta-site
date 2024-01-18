package com.joo.digimon.crawling.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CrawlingResultDto {
    int successCount;
    int failedCount;
    List<CrawlingCardFailedDto> crawlingCardFailedDtoList;

    public CrawlingResultDto() {
        this.successCount = 0;
        this.failedCount = 0;
        this.crawlingCardFailedDtoList = new ArrayList<>();
    }
    public void successCountIncrease(){
        successCount++;
    }

    public void addFailedCrawling(CrawlingCardDto crawlingCardDto, String msg) {
        failedCount++;
        this.crawlingCardFailedDtoList.add(new CrawlingCardFailedDto(crawlingCardDto, msg));
    }

    @Data
    @AllArgsConstructor
    private static class CrawlingCardFailedDto {
        CrawlingCardDto crawlingCardDto;
        String msg;
    }
}
