package com.joo.digimon.card.controller;

import com.joo.digimon.card.dto.CardRequestDto;
import com.joo.digimon.card.dto.CardResponseDto;
import com.joo.digimon.card.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/card")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;
    @GetMapping("/search")

    public ResponseEntity<CardResponseDto> getCards(@ModelAttribute CardRequestDto cardRequestDto){

        return new ResponseEntity<>(cardService.searchCards(cardRequestDto), HttpStatus.OK);
    }
}
