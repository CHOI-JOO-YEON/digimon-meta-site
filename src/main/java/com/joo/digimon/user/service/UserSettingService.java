package com.joo.digimon.user.service;

import com.joo.digimon.user.dto.UserSettingDto;
import com.joo.digimon.user.model.User;

public interface UserSettingService {
    UserSettingDto getUserSetting(User user);
    void updateUserSetting(User user, UserSettingDto dto);
    void initUserSetting(User user);
}
