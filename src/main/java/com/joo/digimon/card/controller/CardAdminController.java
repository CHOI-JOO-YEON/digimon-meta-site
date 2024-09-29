package com.joo.digimon.card.controller;


import com.joo.digimon.card.service.CardAdminService;
import com.joo.digimon.deck.dto.FormatResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/card")
@RequiredArgsConstructor
public class CardAdminController {

    private final CardAdminService cardAdminService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllCard() {
        return new ResponseEntity<>(cardAdminService.getAllCard(), HttpStatus.OK);
    }

}
