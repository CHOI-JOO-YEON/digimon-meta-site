package com.joo.digimon.user.component;

import com.joo.digimon.user.enums.AuthSupplier;
import com.joo.digimon.user.repository.UserRepository;
import com.joo.digimon.user.enums.Role;
import com.joo.digimon.user.model.User;
import com.joo.digimon.user.service.UserSettingService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserInitializationComponent {
    @Value("${admin.id}")
    private String username;
    @Value("${admin.password}")
    private String password;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserSettingService userSettingService;

    @PostConstruct
    @Transactional
    public void init(){
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            User admin = userRepository.save(
                    User.builder()
                            .username(username)
                            .password(bCryptPasswordEncoder.encode(password))
                            .authSupplier(AuthSupplier.USERNAME)
                            .nickName("관리자")
                            .role(Role.ADMIN)
                            .build()
            );
            userSettingService.initUserSetting(admin);
        }
    }
}

