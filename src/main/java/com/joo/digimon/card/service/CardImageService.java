package com.joo.digimon.card.service;

import com.joo.digimon.card.dto.card.UploadWebpResponseDto;
import com.joo.digimon.global.enums.Locale;
import jakarta.transaction.Transactional;

public interface CardImageService {
    int uploadAllImage();
    int getUploadYetCount();

    UploadWebpResponseDto uploadWebpImage(int count, Locale locale);
}
