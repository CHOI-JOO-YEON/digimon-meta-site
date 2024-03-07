package com.joo.digimon.deck.controller;

import com.joo.digimon.deck.dto.RequestDeckDto;
import com.joo.digimon.deck.service.DeckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manager/deck")
@RequiredArgsConstructor
public class DeckManagerController {

    private final DeckService deckService;
    @PostMapping("")
    ResponseEntity<?> exportToTTS(@RequestBody RequestDeckDto requestDeckDto) {
        return new ResponseEntity<>(deckService.exportTTSDeck(requestDeckDto), HttpStatus.OK);
    }
}
