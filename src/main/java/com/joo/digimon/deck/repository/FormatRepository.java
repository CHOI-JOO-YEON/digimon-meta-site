package com.joo.digimon.deck.repository;

import com.joo.digimon.deck.model.Format;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FormatRepository extends JpaRepository<Format,Integer> {
    List<Format> findByEndDateIsAfter(LocalDate localDate, Sort sort);
    Optional<Format> findTopByIsOnlyEnIsNullOrIsOnlyEnIsFalseOrderByStartDateDesc();
    List<Format> findByEndDateGreaterThanEqual(LocalDate latestReleaseCardDate, Sort sort);

}
