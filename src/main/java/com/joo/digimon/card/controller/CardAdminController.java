package com.joo.digimon.card.controller;


import com.joo.digimon.card.dto.*;
import com.joo.digimon.card.service.CardAdminService;
import com.joo.digimon.card.service.CardService;
import com.joo.digimon.deck.dto.FormatRequestDto;
import com.joo.digimon.deck.dto.FormatResponseDto;
import com.joo.digimon.deck.dto.FormatUpdateRequestDto;
import com.joo.digimon.deck.service.FormatService;
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
    private final CardService cardService;
    private final FormatService formatService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllCard() {
        return new ResponseEntity<>(cardAdminService.getAllCard(), HttpStatus.OK);
    }


    @GetMapping("/")
    public ResponseEntity<?> getCards(@ModelAttribute CardRequestDto cardRequestDto) {
        return new ResponseEntity<>(cardService.searchAdminCards(cardRequestDto), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCard(@RequestBody List<CardAdminRequestDto> cardAdminRequestDtoList) {
        cardAdminService.updateCards(cardAdminRequestDtoList);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/notes")
    public ResponseEntity<?> getAllNotes() {
        return new ResponseEntity<>(cardAdminService.getNotes(), HttpStatus.OK);
    }

    @PostMapping("/note")
    public ResponseEntity<?> createNote(@RequestBody CreateNoteDto createNoteDto) {
        return new ResponseEntity<>(cardAdminService.createNote(createNoteDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/note/{note-id}")
    public ResponseEntity<?> deleteNote(@PathVariable(name = "note-id") Integer noteId) {
        return new ResponseEntity<>(cardAdminService.deleteNote(noteId), HttpStatus.OK);
    }

    @PutMapping("/note/update")
    public ResponseEntity<?> updateNote(@RequestBody List<UpdateNoteDto> updateNoteDtoList) {
        return new ResponseEntity<>(cardAdminService.putNotes(updateNoteDtoList), HttpStatus.OK);
    }

    @GetMapping("/types")
    public ResponseEntity<?> getAllTypes() {
        return new ResponseEntity<>(cardAdminService.getAllType(), HttpStatus.OK);
    }

    @DeleteMapping("/type/{type-id}")
    public ResponseEntity<?> deleteType(@PathVariable(name = "type-id") Integer typeId) {
        return new ResponseEntity<>(cardAdminService.deleteType(typeId), HttpStatus.OK);
    }

    @PutMapping("/type/update")
    public ResponseEntity<?> updateType(@RequestBody List<TypeDto> typeDtoList) {
        return new ResponseEntity<>(cardAdminService.putTypes(typeDtoList), HttpStatus.OK);
    }

    @GetMapping("/format")
    public ResponseEntity<?> getAllFormat() {
        return new ResponseEntity<>(formatService.getAllFormat(), HttpStatus.OK);
    }

    @PostMapping("/format")
    ResponseEntity<?> createFormat(@RequestBody FormatRequestDto formatRequestDto) {
        formatService.createFormat(formatRequestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/format")
    ResponseEntity<?> updateFormat(@RequestBody List<FormatUpdateRequestDto> dtos) {
        formatService.updateFormat(dtos);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/format/{format-id}")
    ResponseEntity<?> updateFormat(@PathVariable(name = "format-id") Integer formatId) {
        formatService.deleteFormat(formatId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
