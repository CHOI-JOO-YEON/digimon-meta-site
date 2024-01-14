package com.joo.digimon.crawling.controller;

import com.joo.digimon.crawling.dto.ReflectCardRequestDto;
import com.joo.digimon.crawling.service.CrawlingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/crawling")
@RequiredArgsConstructor
public class CrawlingController {
    private final CrawlingService crawlingService;

    @PostMapping("/page")
    public ResponseEntity<?> crawlingPage(@RequestParam(name = "page-url") String pageUrl) throws IOException {
        return new ResponseEntity<>(crawlingService.crawlAndSaveByUrl(pageUrl), HttpStatus.CREATED);
    }

    @PostMapping("/save")
    public ResponseEntity<?> crawlingCard(@RequestBody List<ReflectCardRequestDto> reflectCardRequestDtoList) {
        return new ResponseEntity<>(crawlingService.saveCardByReflectCardRequestList(reflectCardRequestDtoList), HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getUnReflectCrawlingCardList() {
        return new ResponseEntity<>(crawlingService.getUnreflectedCrawlingCardDtoList(), HttpStatus.OK);
    }

}
