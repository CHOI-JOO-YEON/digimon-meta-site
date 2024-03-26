package com.joo.digimon.limit.dto;

import com.joo.digimon.limit.model.LimitCardEntity;
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

    public GetLimitResponseDto(LimitEntity limitEntity) {
        this.id=limitEntity.getId();
        this.restrictionBeginDate = limitEntity.getRestrictionBeginDate();
        this.banList = new ArrayList<>();
        this.restrictList = new ArrayList<>();
        for (LimitCardEntity limitCardEntity : limitEntity.getLimitCardEntities()) {
            if (limitCardEntity.getAllowedQuantity() == 1) {
                restrictList.add(limitCardEntity.getCardEntity().getCardNo());
            }
            else if (limitCardEntity.getAllowedQuantity() == 0) {
                banList.add(limitCardEntity.getCardEntity().getCardNo());
            }
        }
    }
}
