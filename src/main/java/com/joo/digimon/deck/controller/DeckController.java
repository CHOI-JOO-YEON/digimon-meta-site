package com.joo.digimon.deck.controller;

import com.joo.digimon.annotation.argument_resolver.CurUser;
import com.joo.digimon.deck.dto.RequestDeckDto;
import com.joo.digimon.deck.service.DeckService;
import com.joo.digimon.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/deck")
@RequiredArgsConstructor
public class DeckController {

    private final DeckService deckService;
    @PostMapping("")
    ResponseEntity<?> deckPost(@RequestBody RequestDeckDto requestDeckDto, @CurUser User user){
        System.out.println(requestDeckDto);
        return new ResponseEntity<>(deckService.postDeck(requestDeckDto, user),HttpStatus.OK);
    }

}
