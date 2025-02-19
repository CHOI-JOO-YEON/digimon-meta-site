package com.joo.digimon.limit.repository;

import com.joo.digimon.limit.model.LimitPairCardEntity;
import com.joo.digimon.limit.model.LimitPairEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LimitPairCardRepository extends JpaRepository<LimitPairCardEntity, Integer> {
}
