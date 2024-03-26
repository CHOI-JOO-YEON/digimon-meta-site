package com.joo.digimon.limit.repository;

import com.joo.digimon.limit.model.LimitEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LimitRepository extends JpaRepository<LimitEntity,Integer> {
    @Override
    @EntityGraph("LimitEntity.detail")
    List<LimitEntity> findAll();

    @Override
    @EntityGraph("LimitEntity.detail")
    Optional<LimitEntity> findById(Integer integer);
}
