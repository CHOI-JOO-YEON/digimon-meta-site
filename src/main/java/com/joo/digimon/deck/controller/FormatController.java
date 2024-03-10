package com.joo.digimon.deck.controller;

import com.joo.digimon.deck.dto.FormatRequestDto;
import com.joo.digimon.deck.dto.FormatUpdateRequestDto;
import com.joo.digimon.deck.service.FormatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/format")
public class FormatController {

    private final FormatService formatService;

    @GetMapping
    ResponseEntity<?> getAllFormat(){
        return new ResponseEntity<>(formatService.getFormatList(), HttpStatus.OK);
    }


    @PostMapping
    ResponseEntity<?> createFormat(@RequestBody FormatRequestDto formatRequestDto) {
        formatService.createFormat(formatRequestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/update")
    ResponseEntity<?> createFormat(@RequestBody FormatUpdateRequestDto formatUpdateRequestDto) {
        formatService.updateFormat(formatUpdateRequestDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
