package com.joo.digimon.crawling.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CrawlingResultDto {
    private int successCount;
    private int failedCount;
    private int alreadyReflectCount;
    List<CrawlingCardDto> crawlingCardDtoList;

    public CrawlingResultDto() {
        this.successCount = 0;
        this.failedCount = 0;
        this.crawlingCardDtoList = new ArrayList<>();
    }
    public void successCountIncrease(){
        successCount++;
    }
    public void alreadyReflectCountIncrease() {
        alreadyReflectCount++;}

    public void addFailedCrawling(CrawlingCardDto crawlingCardDto) {
        failedCount++;
        this.crawlingCardDtoList.add(crawlingCardDto);
    }
}
