package com.joo.digimon.card.controller;


import com.joo.digimon.card.dto.card.CardAdminPutDto;
import com.joo.digimon.card.dto.card.CardSearchRequestDto;
import com.joo.digimon.card.dto.card.TypeMergeRequestDto;
import com.joo.digimon.card.dto.note.CreateNoteDto;
import com.joo.digimon.card.dto.note.UpdateNoteDto;
import com.joo.digimon.card.dto.type.TypeDto;
import com.joo.digimon.card.service.CardAdminService;
import com.joo.digimon.card.service.CardService;
import com.joo.digimon.deck.dto.FormatRequestDto;
import com.joo.digimon.deck.dto.FormatUpdateRequestDto;
import com.joo.digimon.deck.service.FormatService;
import com.joo.digimon.limit.dto.LimitPutRequestDto;
import com.joo.digimon.limit.service.LimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class CardAdminController {

    private final CardAdminService cardAdminService;
    private final CardService cardService;
    private final FormatService formatService;
    private final LimitService limitService;

    @GetMapping("/card")
    public ResponseEntity<?> getCards(@ModelAttribute CardSearchRequestDto cardSearchRequestDto) {
        return new ResponseEntity<>(cardService.searchCards(cardSearchRequestDto), HttpStatus.OK);
    }

    @PutMapping("/card/update")
    public ResponseEntity<?> updateCard(@RequestBody List<CardAdminPutDto> cardAdminPutDtoList) {
        cardAdminService.updateCards(cardAdminPutDtoList);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/card/notes")
    public ResponseEntity<?> getAllNotes() {
        return new ResponseEntity<>(cardAdminService.getNotes(), HttpStatus.OK);
    }

    @PostMapping("/card/note")
    public ResponseEntity<?> createNote(@RequestBody CreateNoteDto createNoteDto) {
        return new ResponseEntity<>(cardAdminService.createNote(createNoteDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/card/note/{note-id}")
    public ResponseEntity<?> deleteNote(@PathVariable(name = "note-id") Integer noteId) {
        return new ResponseEntity<>(cardAdminService.deleteNote(noteId), HttpStatus.OK);
    }

    @PutMapping("/card/note/update")
    public ResponseEntity<?> updateNote(@RequestBody List<UpdateNoteDto> updateNoteDtoList) {
        return new ResponseEntity<>(cardAdminService.putNotes(updateNoteDtoList), HttpStatus.OK);
    }

    @GetMapping("/card/traits")
    public ResponseEntity<?> getAllTraits() {
        return new ResponseEntity<>(cardAdminService.getAllTraits(), HttpStatus.OK);
    }
    
    @GetMapping("/card/types")
    public ResponseEntity<?> getAllTypes() {
        return new ResponseEntity<>(cardAdminService.getAllType(), HttpStatus.OK);
    }
    
    @GetMapping("/card/types/{type-id}/detail")
    public ResponseEntity<?> getCardByType(@PathVariable(name = "type-id") Integer typeId) {
        return new ResponseEntity<>(cardAdminService.getCardByTypeId(typeId), HttpStatus.OK);
    }

    @GetMapping("/card/types/detail")
    public ResponseEntity<?> getAllTypesDetail() {
        return new ResponseEntity<>(cardAdminService.getAllTypeDetail(), HttpStatus.OK);
    }

    @DeleteMapping("/card/type/{type-id}")
    public ResponseEntity<?> deleteType(@PathVariable(name = "type-id") Integer typeId) {
        return new ResponseEntity<>(cardAdminService.deleteType(typeId), HttpStatus.OK);
    }

    @PostMapping("/card/type/merge")
    public ResponseEntity<?> mergeTypeToKorean(@RequestBody TypeMergeRequestDto dto) {
        cardAdminService.mergeTypeToKorean(dto);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/card/type/duplicate")
    public ResponseEntity<?> deleteDuplicateCardCombineType() {
        cardAdminService.deleteDuplicateCardCombineType();

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/card/type/update")
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
    ResponseEntity<?> deleteFormat(@PathVariable(name = "format-id") Integer formatId) {
        formatService.deleteFormat(formatId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/limit")
    ResponseEntity<?> getAllLimit() {
        return new ResponseEntity<>(limitService.findAll(), HttpStatus.OK);
    }

    @PostMapping("/limit")
    ResponseEntity<?> createLimit(@RequestBody LimitPutRequestDto limitPutRequestDto) {
        limitService.createLimit(limitPutRequestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/limit")
    ResponseEntity<?> updateLimit(@RequestBody List<LimitPutRequestDto> limitPutRequestDtos) {
        limitService.updateLimits(limitPutRequestDtos);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/limit/{limit-id}")
    ResponseEntity<?> deleteLimit(@PathVariable(name = "limit-id") Integer limitId) {
        limitService.deleteLimit(limitId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/card/pr")
    ResponseEntity<?> createPr(@RequestParam(value = "message") String message) {
        return cardAdminService.createCardJsonUpdateToGitHubPR(message) ? new ResponseEntity<>(HttpStatus.CREATED) : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    


}
