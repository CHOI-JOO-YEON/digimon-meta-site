package com.joo.digimon.collect.controller;

import com.joo.digimon.global.annotation.argument_resolver.CurUser;
import com.joo.digimon.collect.dto.UpdateCardCollectRequestDto;
import com.joo.digimon.collect.service.CardCollectService;
import com.joo.digimon.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/card-collect")
@RequiredArgsConstructor
public class CardCollectController {

    private final CardCollectService cardCollectService;

    @PostMapping("")
    ResponseEntity<?> updateCardCollect(@CurUser User user, @RequestBody List<UpdateCardCollectRequestDto> updateCardCollectRequestDtoList) {
        cardCollectService.updateUserCardCollection(user, updateCardCollectRequestDtoList);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    ResponseEntity<?> getCollection(@CurUser User user) {
        return new ResponseEntity<>(cardCollectService.getUserCardCollection(user), HttpStatus.OK);
    }
}
