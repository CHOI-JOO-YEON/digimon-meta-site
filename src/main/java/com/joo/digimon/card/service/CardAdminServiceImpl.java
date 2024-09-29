package com.joo.digimon.card.service;

import com.joo.digimon.card.dto.CardDto;
import com.joo.digimon.card.model.CardImgEntity;
import com.joo.digimon.card.repository.CardImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardAdminServiceImpl implements CardAdminService {
    private final CardImgRepository cardImgRepository;
    @Value("${domain.url}")
    private String prefixUrl;

    @Override
    public List<CardDto> getAllCard() {
        List<CardImgEntity> cardImgRepositoryAll = cardImgRepository.findAll();

        List<CardDto> allCards = new ArrayList<>();

        for (CardImgEntity cardImgEntity : cardImgRepositoryAll) {
            allCards.add(new CardDto(cardImgEntity, prefixUrl));
        }

        return allCards;
    }
}
