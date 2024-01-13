package com.joo.digimon.card.repository;

import com.joo.digimon.card.model.CardImgEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardImgRepository extends JpaRepository<CardImgEntity,Integer> {
}
