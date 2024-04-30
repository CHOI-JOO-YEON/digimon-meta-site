package com.joo.digimon.card.controller;

import com.joo.digimon.card.dto.CardRequestDto;
import com.joo.digimon.card.dto.CardResponseDto;
import com.joo.digimon.card.dto.NoteDto;
import com.joo.digimon.card.service.CardService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/card")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;
    @GetMapping("/search")

    public ResponseEntity<CardResponseDto> getCards(@ModelAttribute CardRequestDto cardRequestDto){

        return new ResponseEntity<>(cardService.searchCards(cardRequestDto), HttpStatus.OK);
    }

    @GetMapping("/note")
    public ResponseEntity<List<NoteDto>> getNotes() {
        return new ResponseEntity<>(cardService.getNotes(), HttpStatus.OK);
    }

    @GetMapping("/types")
    public ResponseEntity<?> getTypes(){
        return new ResponseEntity<>(cardService.getTypes(), HttpStatus.OK);
    }

}
