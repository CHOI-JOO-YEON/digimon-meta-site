package com.joo.digimon.collect.dto;

import com.joo.digimon.collect.model.UserCard;
import lombok.Data;

@Data
public class CardCollectDto {
    Integer cardImgId;
    Integer quantity;
    String cardNo;
    public CardCollectDto(UserCard userCard) {
        this.cardImgId = userCard.getCardImgEntity().getId();
        this.quantity = userCard.getQuantity();
        this.cardNo=userCard.getCardImgEntity().getCardEntity().getCardNo();
    }
}
