package com.joo.digimon.card.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.joo.digimon.card.model.CardImgEntity;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminCardResponseDto {


    Integer totalPages;
    Integer currentPage;
    Integer totalElements;
    List<AdminCardDto> cards;


    public AdminCardResponseDto(Page<CardImgEntity> page, String prefixUrl) {
        this.totalPages = page.getTotalPages();
        this.totalElements = Math.toIntExact(page.getTotalElements());
        this.currentPage = page.getNumber();
        cards = new ArrayList<>();
        for (CardImgEntity cardImgEntity : page) {
            cards.add(new AdminCardDto(cardImgEntity, prefixUrl));
        }
    }


    public AdminCardResponseDto(List<CardImgEntity> entities, String prefixUrl, int currentPage, int totalElements, int totalPages) {
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.currentPage = currentPage;
        cards = new ArrayList<>();
        for (CardImgEntity cardImgEntity : entities) {
            cards.add(new AdminCardDto(cardImgEntity, prefixUrl));
        }
    }


}
