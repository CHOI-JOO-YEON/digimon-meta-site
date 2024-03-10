package com.joo.digimon.deck.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "FORMATS_TB")
public class Format {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String name;

//    @ManyToOne
//    @JoinColumn(name = "languages_tb_id")
//    Language language;

    LocalDate startDate;
    LocalDate endDate;
}
