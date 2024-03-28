package com.joo.digimon.collect.service;

import com.joo.digimon.collect.dto.CardCollectDto;
import com.joo.digimon.collect.dto.UpdateCardCollectRequestDto;
import com.joo.digimon.user.model.User;

import java.util.List;

public interface CardCollectService {
     List<CardCollectDto> getUserCardCollection(User user);

     void updateUserCardCollection(User user, List<UpdateCardCollectRequestDto> updateCardCollectRequestDtoList);
}
