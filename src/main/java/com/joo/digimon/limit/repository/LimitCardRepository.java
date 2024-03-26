package com.joo.digimon.limit.repository;

import com.joo.digimon.limit.model.LimitCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LimitCardRepository extends JpaRepository<LimitCardEntity, Integer> {
}
