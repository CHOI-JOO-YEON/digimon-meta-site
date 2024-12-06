package com.joo.digimon.card.service;

import jakarta.transaction.Transactional;

public interface CardImageService {
    int uploadAllImage();
    int getUploadYetCount();
}
