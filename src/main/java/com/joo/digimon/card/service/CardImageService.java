package com.joo.digimon.card.service;

import jakarta.transaction.Transactional;

public interface CardImageService {
    int uploadNotUploadYetKorCardImages();

    @Transactional
    int uploadAllImage();

    @Transactional
    int uploadNotUploadYetEnCardImages();

    @Transactional
    int uploadNotUploadYetJpCardImages();

    int getUploadYetCount();
}
