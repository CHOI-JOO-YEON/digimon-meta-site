package com.joo.digimon.card.repository;

import com.joo.digimon.card.model.ParallelCardImgEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParallelCardImgRepository extends JpaRepository<ParallelCardImgEntity,Integer> {
}
