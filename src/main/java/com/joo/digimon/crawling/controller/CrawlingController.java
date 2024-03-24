package com.joo.digimon.crawling.controller;

import com.joo.digimon.card.service.CardImageService;
import com.joo.digimon.crawling.dto.UpdateCrawlingRequestDto;
import com.joo.digimon.crawling.service.CrawlingEnService;
import com.joo.digimon.crawling.service.CrawlingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/crawling")
@RequiredArgsConstructor
public class CrawlingController {
    private final CrawlingService crawlingService;
    private final CardImageService cardImageService;
    private final CrawlingEnService crawlingEnService;

    @PostMapping("/page")
    public ResponseEntity<?> crawlingPage(@RequestParam(name = "page-url") String pageUrl) throws IOException {
        return new ResponseEntity<>(crawlingService.crawlAndSaveByUrl(pageUrl), HttpStatus.CREATED);
    }

    @PostMapping("/page/en")
    public ResponseEntity<?> crawlingPageEn(@RequestParam(name = "page-url") String pageUrl) throws IOException {
        return new ResponseEntity<>(crawlingEnService.crawlAndSaveByUrl(pageUrl), HttpStatus.CREATED);
    }

    @PostMapping("/page/en/only-note")
    public ResponseEntity<?> crawlingPageEnOnlyNote(@RequestParam(name = "page-url") String pageUrl, @RequestParam(name="note") String note) throws IOException {
        return new ResponseEntity<>(crawlingEnService.crawlAndSaveByUrl(pageUrl, note), HttpStatus.CREATED);
    }
//    @PostMapping("/save")
//    public ResponseEntity<?> crawlingCard(@RequestBody List<ReflectCardRequestDto> reflectCardRequestDtoList) {
//        return new ResponseEntity<>(crawlingService.saveCardByReflectCardRequestList(reflectCardRequestDtoList), HttpStatus.CREATED);
//    }


    @PostMapping("/update")
    public ResponseEntity<?> crawlingCard(@RequestBody List<UpdateCrawlingRequestDto> updateCrawlingRequestDtoList) {
        return new ResponseEntity<>(crawlingService.updateCrawlingEntityAndSaveCard(updateCrawlingRequestDtoList), HttpStatus.OK);
    }
    @GetMapping("/list")
    public ResponseEntity<?> getUnReflectCrawlingCardList(@RequestParam("size") Integer size) {
        return new ResponseEntity<>(crawlingService.getUnreflectedCrawlingCardDtoList(size), HttpStatus.OK);
    }

    @PostMapping("/upload-all")
    public ResponseEntity<?> uploadAll() {
        return new ResponseEntity<>(cardImageService.uploadNotUploadYetKorCardImages(), HttpStatus.OK);
    }

    @PostMapping("/upload-all/en")
    public ResponseEntity<?> uploadAllEn() {
        return new ResponseEntity<>(cardImageService.uploadNotUploadYetEnCardImages(), HttpStatus.OK);
    }

}
