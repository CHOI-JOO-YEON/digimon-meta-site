package com.joo.digimon.limit.repository;

import com.joo.digimon.limit.model.LimitCardEntity;
import com.joo.digimon.limit.model.LimitPairEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LimitPairRepository extends JpaRepository<LimitPairEntity, Integer> {
}
