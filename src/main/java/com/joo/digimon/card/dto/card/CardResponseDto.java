package com.joo.digimon.card.dto.card;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.joo.digimon.card.model.CardImgEntity;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardResponseDto {


    Integer totalPages;
    Integer currentPage;
    Integer totalElements;
    List<CardVo> cards;


    public CardResponseDto(Page<CardImgEntity> page, String prefixUrl) {
        this.totalPages = page.getTotalPages();
        this.totalElements = Math.toIntExact(page.getTotalElements());
        this.currentPage = page.getNumber();
        cards = new ArrayList<>();
        for (CardImgEntity cardImgEntity : page) {
            cards.add(new CardVo(cardImgEntity, prefixUrl));
        }
    }


    public CardResponseDto(List<CardImgEntity> entities, String prefixUrl, int currentPage, int totalElements, int totalPages) {
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.currentPage = currentPage;
        cards = new ArrayList<>();
        for (CardImgEntity cardImgEntity : entities) {
            cards.add(new CardVo(cardImgEntity, prefixUrl));
        }
    }


}
