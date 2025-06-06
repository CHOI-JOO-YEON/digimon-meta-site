package com.joo.digimon.limit.dto;


import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class LimitPutRequestDto {
    Integer id;
    LocalDate restrictionBeginDate;
    List<String> banList;
    List<String> restrictList;
    List<LimitPair> limitPairList;
}
