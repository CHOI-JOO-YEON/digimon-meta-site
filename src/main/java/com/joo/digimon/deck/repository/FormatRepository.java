package com.joo.digimon.deck.repository;

import com.joo.digimon.deck.model.Format;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormatRepository extends JpaRepository<Format,Integer> {

}
