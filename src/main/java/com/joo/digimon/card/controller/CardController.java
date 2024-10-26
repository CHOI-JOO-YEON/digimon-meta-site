package com.joo.digimon.card.controller;

import com.joo.digimon.card.dto.CardSearchRequestDto;
import com.joo.digimon.card.dto.CardResponseDto;
import com.joo.digimon.card.dto.ResponseNoteDto;
import com.joo.digimon.card.service.CardService;
import com.joo.digimon.card.service.UseCardService;
import com.joo.digimon.deck.dto.FormatResponseDto;
import com.joo.digimon.deck.service.FormatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/card")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;
    private final UseCardService useCardService;
    private final FormatService formatService;

    @GetMapping("/search")
    public ResponseEntity<CardResponseDto> getCards(@ModelAttribute CardSearchRequestDto cardSearchRequestDto){

        return new ResponseEntity<>(cardService.searchCards(cardSearchRequestDto), HttpStatus.OK);
    }

    @GetMapping("/note")
    public ResponseEntity<List<ResponseNoteDto>> getNotes() {
        return new ResponseEntity<>(cardService.getNotes(), HttpStatus.OK);
    }

    @GetMapping("/types")
    public ResponseEntity<?> getTypes(){
        return new ResponseEntity<>(cardService.getTypes(), HttpStatus.OK);
    }

    @GetMapping("/use")
    public ResponseEntity<?> getUseCards(@RequestParam("cardImgId") Integer cardImgId){
        FormatResponseDto formatResponseDto = formatService.getCurrentFormat();
         useCardService.findTopUsedCardsWithACard(cardImgId, formatResponseDto.getId());

        return new ResponseEntity<>(useCardService.findTopUsedCardsWithACard(cardImgId, formatResponseDto.getId()), HttpStatus.OK);
    }

}
