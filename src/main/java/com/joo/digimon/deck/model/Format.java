package com.joo.digimon.deck.model;

import com.joo.digimon.deck.dto.FormatRequestDto;
import com.joo.digimon.deck.dto.FormatUpdateRequestDto;
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
    Boolean isOnlyEn;
    LocalDate startDate;
    LocalDate endDate;
    public void update(FormatUpdateRequestDto dto){
        this.name = dto.getFormatName();
        this.isOnlyEn = dto.getIsOnlyEn();
        this.startDate = dto.getStartDate();
        this.endDate = dto.getEndDate();
    }
}
