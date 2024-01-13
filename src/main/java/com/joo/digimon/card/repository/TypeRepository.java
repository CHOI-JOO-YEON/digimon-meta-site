package com.joo.digimon.card.repository;

import com.joo.digimon.card.model.TypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeRepository extends JpaRepository<TypeEntity,Integer> {
}
