package com.joo.digimon.deck.controller;

import com.joo.digimon.annotation.argument_resolver.CurUser;
import com.joo.digimon.deck.dto.DeckImportRequestDto;
import com.joo.digimon.deck.dto.DeckSearchParameter;
import com.joo.digimon.deck.dto.RequestDeckDto;
import com.joo.digimon.deck.dto.ResponseDeckDto;
import com.joo.digimon.deck.service.DeckService;
import com.joo.digimon.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/deck")
@RequiredArgsConstructor
public class DeckController {

    private final DeckService deckService;
    @PostMapping()
    ResponseEntity<?> deckPost(@RequestBody RequestDeckDto requestDeckDto, @CurUser User user){
        return new ResponseEntity<>(deckService.postDeck(requestDeckDto, user),HttpStatus.OK);
    }

//    @GetMapping()
//    ResponseEntity<?> findDecks(@CurUser User user, @RequestBody DeckSearchParameter deckSearchParameter) {
//        return new ResponseEntity<>(deckService.getDecks(deckSearchParameter, user), HttpStatus.OK);
//    }

    @GetMapping()
    ResponseEntity<?> findDecks(@CurUser User user, @ModelAttribute DeckSearchParameter deckSearchParameter) {
        if (deckSearchParameter.getIsMyDeck()) {
            return new ResponseEntity<>(deckService.finMyDecks(user, deckSearchParameter), HttpStatus.OK);
        }
        return new ResponseEntity<>(deckService.findDecks(deckSearchParameter), HttpStatus.OK);

    }
    @GetMapping("/detail")
    ResponseEntity<?> findDecks(@CurUser User user,@RequestParam("deck-id") Integer deckId) {
        return new ResponseEntity<>(deckService.findDeck(deckId, user), HttpStatus.OK);
    }
    @PostMapping("/import")
    ResponseEntity<?> importDecks(@RequestBody DeckImportRequestDto deckImportRequestDto) {
        ResponseDeckDto responseDeckDto = deckService.importDeck(deckImportRequestDto);
        return new ResponseEntity<>(responseDeckDto,HttpStatus.OK);
    }
    @PostMapping("/delete")
    ResponseEntity<?> importDecks(@RequestParam("deck-id") Integer deckId, @CurUser User user) {
        deckService.deleteDeck(deckId,user);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
