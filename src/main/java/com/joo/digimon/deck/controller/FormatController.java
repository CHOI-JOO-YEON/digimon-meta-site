package com.joo.digimon.deck.controller;

import com.joo.digimon.deck.dto.FormatRequestDto;
import com.joo.digimon.deck.dto.FormatUpdateRequestDto;
import com.joo.digimon.deck.service.DeckService;
import com.joo.digimon.deck.service.FormatService;
import com.joo.digimon.global.annotation.argument_resolver.CurUser;
import com.joo.digimon.user.model.User;
import lombok.Getter;
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
    private final DeckService deckService;

    @GetMapping
    ResponseEntity<?> getAllFormat(@RequestParam("date") LocalDate latestReleaseCardDate){
        return new ResponseEntity<>(formatService.getFormatList(latestReleaseCardDate), HttpStatus.OK);
    }
    
    @GetMapping("/deck-count")
    ResponseEntity<?> getDeckCount(@CurUser User user){
        return new ResponseEntity<>(deckService.getFormatDeckCount(user), HttpStatus.OK);   
    }
}
