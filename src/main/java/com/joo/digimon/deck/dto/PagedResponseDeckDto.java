package com.joo.digimon.deck.dto;

import com.joo.digimon.deck.model.DeckEntity;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class PagedResponseDeckDto {
    private List<ResponseDeckDto> decks;
    private int currentPage;
    private int totalPages;
    private long totalElements;

    public PagedResponseDeckDto(Page<DeckEntity> deckEntityPage) {
        this.decks = new ArrayList<>();
        this.currentPage = deckEntityPage.getNumber();
        this.totalPages = deckEntityPage.getTotalPages();
        this.totalElements = deckEntityPage.getTotalElements();
        for (DeckEntity deck : deckEntityPage) {
            decks.add(new ResponseDeckDto(deck));
        }
    }
}
