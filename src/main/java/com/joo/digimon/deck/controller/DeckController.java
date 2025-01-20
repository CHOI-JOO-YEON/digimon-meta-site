package com.joo.digimon.deck.controller;

import com.joo.digimon.global.annotation.argument_resolver.CurUser;
import com.joo.digimon.deck.dto.*;
import com.joo.digimon.deck.service.DeckService;
import com.joo.digimon.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/deck")
@RequiredArgsConstructor
public class DeckController {

    private final DeckService deckService;
    @PostMapping()
    ResponseEntity<?> deckPost(@RequestBody RequestDeckDto requestDeckDto, @CurUser User user){
        return new ResponseEntity<>(deckService.postDeck(requestDeckDto, user),HttpStatus.OK);
    }

    @GetMapping()
    ResponseEntity<?> findDecks(@CurUser(required = false) User user, @ModelAttribute DeckSearchParameter deckSearchParameter) {
        if (deckSearchParameter.getIsMyDeck()) {
            return new ResponseEntity<>(deckService.finMyDecks(user, deckSearchParameter), HttpStatus.OK);
        }
        return new ResponseEntity<>(deckService.findDecks(deckSearchParameter), HttpStatus.OK);

    }
    @GetMapping("/detail")
    ResponseEntity<?> findDeck(@CurUser(required = false) User user,@RequestParam("deck-id") Integer deckId) {
        return new ResponseEntity<>(deckService.findDeck(deckId, user), HttpStatus.OK);
    }
    @PostMapping("/import")
    ResponseEntity<?> importDecks(@RequestBody DeckImportRequestDto deckImportRequestDto) {
        ResponseDeckDto responseDeckDto = deckService.importDeck(deckImportRequestDto);
        return new ResponseEntity<>(responseDeckDto,HttpStatus.OK);
    }

    @PostMapping("/import/this")
    ResponseEntity<?> importDeckByThisSite(@RequestBody DeckImportThisSiteRequestDto deckImportThisSiteRequestDto) {
        ResponseDeckDto responseDeckDto = deckService.importDeck(deckImportThisSiteRequestDto);
        return new ResponseEntity<>(responseDeckDto,HttpStatus.OK);
    }

    @GetMapping("/all")
    ResponseEntity<?> findAllMyDecks(@CurUser User user) {
        return new ResponseEntity<>(deckService.findAllMyDeck(user), HttpStatus.OK);

    }
    @PostMapping("/delete")
    ResponseEntity<?> importDecks(@RequestParam("deck-id") Integer deckId, @CurUser User user) {
        deckService.deleteDeck(deckId,user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @GetMapping("/import/qr")
    ResponseEntity<?> importDeckByQr(@RequestParam("deck") String deckString) {
        Map<Integer, Integer> deckMap = parseDeckString(deckString);

        return ResponseEntity.ok(deckService.importDeckByDeckMap(deckMap));
    }

    private Map<Integer, Integer> parseDeckString(String deckString) {
        Map<Integer, Integer> result = new HashMap<>();
        String[] pairs = deckString.split(",");
        for (String pair : pairs) {
            if (pair.contains("=")) {
                String[] parts = pair.split("=");
                if (parts.length == 2) {
                    Integer key = Integer.parseInt(parts[0]);
                    Integer value = Integer.parseInt(parts[1]);
                    result.put(key, value);
                }
            }
        }
        return result;
    }


}
