package com.joo.digimon.card.repository;

import com.joo.digimon.card.model.NoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<NoteEntity,Integer> {
    Optional<NoteEntity> findByName(String name);

    List<NoteEntity> findByIsDisableFalseOrIsDisableNullOrderByReleaseDate();
}
