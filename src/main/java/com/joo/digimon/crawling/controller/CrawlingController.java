package com.joo.digimon.crawling.controller;

import com.joo.digimon.card.service.CardImageService;
import com.joo.digimon.crawling.dto.UpdateCrawlingRequestDto;
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


    @PostMapping("/page")
    public ResponseEntity<?> crawlingPageEnOnlyNote(@RequestParam(name = "page-url") String pageUrl, @RequestParam(name = "locale") String locale, @RequestParam(name = "note", required = false) String note) throws IOException {
        return new ResponseEntity<>(crawlingService.crawlAndSaveByUrl(pageUrl, locale, note), HttpStatus.CREATED);
    }

    @PostMapping("/upload-all")
    public ResponseEntity<?> uploadAll() {
        return new ResponseEntity<>(cardImageService.uploadAllImage(), HttpStatus.OK);
    }

    @GetMapping("/upload-yet-count")
    public ResponseEntity<?> uploadYetCount() {
        return new ResponseEntity<>(cardImageService.getUploadYetCount(),HttpStatus.OK);
    }

}
