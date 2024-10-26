package com.joo.digimon.limit.controller;

import com.joo.digimon.limit.service.LimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/limit")
public class LimitController {
    private final LimitService limitService;
    @GetMapping("")
    ResponseEntity<?> getLimitList(){
        return new ResponseEntity<>(limitService.findAll(),HttpStatus.OK);
    }
}
