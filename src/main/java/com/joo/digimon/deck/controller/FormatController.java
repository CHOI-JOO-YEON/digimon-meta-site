package com.joo.digimon.deck.controller;

import com.joo.digimon.deck.dto.FormatRequestDto;
import com.joo.digimon.deck.dto.FormatUpdateRequestDto;
import com.joo.digimon.deck.service.FormatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/format")
public class FormatController {

    private final FormatService formatService;

    @GetMapping
    ResponseEntity<?> getAllFormat(@RequestParam("date") LocalDate latestReleaseCardDate){
        return new ResponseEntity<>(formatService.getFormatList(latestReleaseCardDate), HttpStatus.OK);
    }
}
