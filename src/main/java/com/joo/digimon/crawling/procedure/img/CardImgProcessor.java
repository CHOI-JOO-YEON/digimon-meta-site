package com.joo.digimon.crawling.procedure.img;

import com.joo.digimon.card.model.CardEntity;
import com.joo.digimon.card.model.NoteEntity;
import com.joo.digimon.crawling.dto.ReflectCardRequestDto;

public interface CardImgProcessor {
    void process(ReflectCardRequestDto dto, CardEntity cardEntity, NoteEntity noteEntity);
}
