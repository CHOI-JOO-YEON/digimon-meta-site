package com.joo.digimon.user.service;

import com.joo.digimon.global.enums.Locale;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joo.digimon.user.dto.SortCriterionDto;
import com.joo.digimon.user.dto.UserSettingDto;
import com.joo.digimon.user.model.User;
import com.joo.digimon.user.model.UserSetting;
import com.joo.digimon.user.repository.UserSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserSettingServiceImpl implements UserSettingService {
    private final UserSettingRepository userSettingRepository;
    private final ObjectMapper objectMapper;

    @Override
    public UserSettingDto getUserSetting(User user) {
        UserSetting setting = userSettingRepository.findByUser(user)
                .orElseGet(() -> userSettingRepository.save(createDefaultSetting(user)));
        return toDto(setting);
    }

    @Override
    public void updateUserSetting(User user, UserSettingDto dto) {
        UserSetting setting = userSettingRepository.findByUser(user)
                .orElseGet(() -> userSettingRepository.save(createDefaultSetting(user)));

        if (dto.getLocalePriority() != null) {
            String lp = dto.getLocalePriority().stream()
                    .map(Enum::name)
                    .collect(Collectors.joining(","));
            setting.setLocalePriority(lp);
        }
        if (dto.getDefaultLimitId() != null) {
            setting.setDefaultLimitId(dto.getDefaultLimitId());
        }
        if (dto.getStrictDeck() != null) {
            setting.setIsStrictDeck(dto.getStrictDeck());
        }
        if (dto.getSortPriority() != null) {
            setting.setSortPriority(toJson(dto.getSortPriority()));
        }
        userSettingRepository.save(setting);
    }

    @Override
    public void initUserSetting(User user) {
        userSettingRepository.findByUser(user)
                .orElseGet(() -> userSettingRepository.save(createDefaultSetting(user)));
    }

    private UserSetting createDefaultSetting(User user) {
        return UserSetting.builder()
                .user(user)
                .localePriority("KOR,ENG,JPN")
                .isStrictDeck(true)
                .defaultLimitId(null)
                .sortPriority(toJson(defaultSortPriority()))
                .build();
    }

    private UserSettingDto toDto(UserSetting setting) {
        List<Locale> locales = new ArrayList<>();
        if (setting.getLocalePriority() != null && !setting.getLocalePriority().isEmpty()) {
            for (String s : setting.getLocalePriority().split(",")) {
                locales.add(Locale.valueOf(s));
            }
        }
        Integer limitId = setting.getDefaultLimitId();
        List<SortCriterionDto> criteria = fromJson(setting.getSortPriority());
        return new UserSettingDto(locales, limitId, setting.getIsStrictDeck(), criteria);
    }

    private String toJson(List<SortCriterionDto> list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private List<SortCriterionDto> fromJson(String json) {
        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<SortCriterionDto>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private List<SortCriterionDto> defaultSortPriority() {
        List<SortCriterionDto> list = new ArrayList<>();
        list.add(new SortCriterionDto("cardType", true, Map.of("DIGIMON", 1, "TAMER", 2, "OPTION", 3)));
        list.add(new SortCriterionDto("lv", true, null));
        list.add(new SortCriterionDto("color1", true,
                Map.of("RED", 1, "BLUE", 2, "YELLOW", 3, "GREEN", 4, "BLACK", 5, "PURPLE", 6, "WHITE", 7)));
        list.add(new SortCriterionDto("color2", true,
                Map.of("RED", 1, "BLUE", 2, "YELLOW", 3, "GREEN", 4, "BLACK", 5, "PURPLE", 6, "WHITE", 7)));
        list.add(new SortCriterionDto("playCost", true, null));
        list.add(new SortCriterionDto("sortString", true, null));
        list.add(new SortCriterionDto("isParallel", true, null));
        list.add(new SortCriterionDto("dp", true, null));
        list.add(new SortCriterionDto("cardName", true, null));
        list.add(new SortCriterionDto("releaseDate", true, null));
        list.add(new SortCriterionDto("hasXAntibody", true, null));
        return list;
    }
}

