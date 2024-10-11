package com.joo.digimon.card.repository;

import com.joo.digimon.card.model.TypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TypeRepository extends JpaRepository<TypeEntity,Integer> {
    Optional<TypeEntity> findByName(String name);

    @Override
    List<TypeEntity> findAll();
}
