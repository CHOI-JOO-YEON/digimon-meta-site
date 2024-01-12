package com.joo.digimon.user.repository;

import com.joo.digimon.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByUsername(String username);

    Optional<User> findByOauthId(String oAuthId);
}
