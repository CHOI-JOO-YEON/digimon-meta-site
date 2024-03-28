package com.joo.digimon.collect.service;

import com.joo.digimon.card.model.CardImgEntity;
import com.joo.digimon.card.repository.CardImgRepository;
import com.joo.digimon.collect.dto.CardCollectDto;
import com.joo.digimon.collect.dto.UpdateCardCollectRequestDto;
import com.joo.digimon.collect.model.UserCard;
import com.joo.digimon.collect.repository.UserCardRepository;
import com.joo.digimon.user.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardCollectServiceImpl implements CardCollectService{
    private final UserCardRepository userCardRepository;
    private final CardImgRepository cardImgRepository;
    @Override
    @Transactional
    public List<CardCollectDto> getUserCardCollection(User user) {
        List<UserCard> userCardList = userCardRepository.findByUser(user);
        List<CardCollectDto> cardCollectDtoList = new ArrayList<>();
        for (UserCard userCard : userCardList) {
            cardCollectDtoList.add(new CardCollectDto(userCard));
        }

        return cardCollectDtoList;
    }

    @Override
    @Transactional
    public void updateUserCardCollection(User user, List<UpdateCardCollectRequestDto> updateCardCollectRequestDtoList) {
        List<Integer> cardImgIds = updateCardCollectRequestDtoList.stream()
                .map(UpdateCardCollectRequestDto::getCardImgId)
                .toList();
        Map<Integer, CardImgEntity> cardImgEntityMap = cardImgRepository.findByIdIn(cardImgIds).stream()
                .collect(Collectors.toMap(CardImgEntity::getId, Function.identity()));

        List<UserCard> userCards = updateCardCollectRequestDtoList.stream()
                .map(update -> UserCard.builder()
                        .cardImgEntity(cardImgEntityMap.get(update.getCardImgId()))
                        .user(user)
                        .quantity(update.getQuantity())
                        .build())
                .collect(Collectors.toList());

        userCardRepository.saveAll(userCards);
    }
}
