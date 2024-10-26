package com.joo.digimon.limit.dto;


import jakarta.validation.Valid;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.List;

@Data
public class LimitPutRequestDto {
    Integer id;
    LocalDate restrictionBeginDate;
    List<String> banList;
    List<String> restrictList;
}
