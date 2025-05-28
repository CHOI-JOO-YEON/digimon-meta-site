package com.joo.digimon.user.repository;

import com.joo.digimon.user.model.User;
import com.joo.digimon.user.model.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSettingRepository extends JpaRepository<UserSetting, Integer> {
    Optional<UserSetting> findByUser(User user);
}
