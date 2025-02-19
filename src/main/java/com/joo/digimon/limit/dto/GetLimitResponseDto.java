package com.joo.digimon.limit.dto;

import com.joo.digimon.limit.model.LimitEntity;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class GetLimitResponseDto {
    Integer id;
    LocalDate restrictionBeginDate;
    List<String> banList;
    List<String> restrictList;
    List<LimitPair> limitPairList;

    public GetLimitResponseDto(LimitEntity limitEntity) {
        this.id=limitEntity.getId();
        this.restrictionBeginDate = limitEntity.getRestrictionBeginDate();
        this.banList = new ArrayList<>();
        this.restrictList = new ArrayList<>();
        this.limitPairList = new ArrayList<>();
        
        limitEntity.getLimitCardEntities().forEach(limitCardEntity ->
                (limitCardEntity.getAllowedQuantity() == 1 ? restrictList : banList)
                        .add(limitCardEntity.getCardEntity().getCardNo())
        );

        limitEntity.getLimitPairEntities().forEach(limitPairEntity -> {
            limitPairList.add(new LimitPair(limitPairEntity));
        });
    }
}
