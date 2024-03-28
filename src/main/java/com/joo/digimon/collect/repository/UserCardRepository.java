package com.joo.digimon.collect.repository;

import com.joo.digimon.card.model.CardImgEntity;
import com.joo.digimon.collect.model.UserCard;
import com.joo.digimon.user.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCardRepository extends JpaRepository<UserCard, Long> {

    @EntityGraph("UserCard.detail")
    List<UserCard> findByUser(User user);
}
