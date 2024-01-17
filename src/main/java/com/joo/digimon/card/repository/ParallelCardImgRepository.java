package com.joo.digimon.card.repository;

import com.joo.digimon.card.model.ParallelCardImgEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParallelCardImgRepository extends JpaRepository<ParallelCardImgEntity,Integer> {
    @EntityGraph("ParallelCardImgEntity.detail")
    List<ParallelCardImgEntity> findByUploadUrlIsNull();
}
