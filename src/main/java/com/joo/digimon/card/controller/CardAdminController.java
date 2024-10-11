package com.joo.digimon.card.controller;


import com.joo.digimon.card.dto.*;
import com.joo.digimon.card.service.CardAdminService;
import com.joo.digimon.card.service.CardService;
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
        return new ResponseEntity<>(cardService.getNotes(), HttpStatus.OK);
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
}
