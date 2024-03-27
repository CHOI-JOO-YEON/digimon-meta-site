package com.joo.digimon.crawling.repository;

import com.joo.digimon.crawling.model.DeletedEnCardImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeletedEnCardImgRepository extends JpaRepository<DeletedEnCardImg,Integer> {
}
