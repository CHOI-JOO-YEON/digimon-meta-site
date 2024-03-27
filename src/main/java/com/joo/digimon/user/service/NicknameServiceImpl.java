package com.joo.digimon.user.service;

import com.joo.digimon.card.model.CardEntity;
import com.joo.digimon.card.repository.CardRepository;
import com.joo.digimon.crawling.enums.CardType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NicknameServiceImpl implements NicknameService {
    private final CardRepository cardRepository;


    @Override
    public String generateNickname() {
        List<CardEntity> allEntities = cardRepository.findByCardTypeAndIsOnlyEnCardIsNullOrIsOnlyEnCardIsFalse(CardType.DIGIMON);
        if (allEntities.isEmpty()) {
            return "이그드라실";
        }
        int randomIndex = (int) (Math.random() * allEntities.size());
        return allEntities.get(randomIndex).getCardName();
    }

}
