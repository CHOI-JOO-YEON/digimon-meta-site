package com.joo.digimon.user.service;

import com.joo.digimon.global.enums.Locale;
import com.joo.digimon.user.dto.UserSettingDto;
import com.joo.digimon.user.model.User;
import com.joo.digimon.user.model.UserSetting;
import com.joo.digimon.user.repository.UserSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserSettingServiceImpl implements UserSettingService {
    private final UserSettingRepository userSettingRepository;

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
        if (dto.getSortPriority() != null) {
            String sp = String.join(",", dto.getSortPriority());
            setting.setSortPriority(sp);
        }
        if (dto.getStrictDeck() != null) {
            setting.setIsStrictDeck(dto.getStrictDeck());
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
                .sortPriority(String.join(",",
                        "cardType","lv","color1","color2","playCost","sortString",
                        "isParallel","dp","cardName","releaseDate","hasXAntibody"))
                .isStrictDeck(true)
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
        List<String> sortPriority = new ArrayList<>();
        if (setting.getSortPriority() != null && !setting.getSortPriority().isEmpty()) {
            for (String s : setting.getSortPriority().split(",")) {
                sortPriority.add(s);
            }
        }
        return new UserSettingDto(locales, limitId, setting.getIsStrictDeck(), sortPriority);
    }
}

