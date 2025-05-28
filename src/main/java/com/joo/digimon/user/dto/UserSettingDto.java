package com.joo.digimon.user.dto;

import com.joo.digimon.global.enums.Locale;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSettingDto {
    private List<Locale> localePriority;
    private Integer defaultLimitId;
    private Boolean strictDeck;
    private List<String> sortPriority;
}
