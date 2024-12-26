package com.joo.digimon.card.service;

import com.joo.digimon.card.model.CardImgEntity;
import com.joo.digimon.card.model.EnglishCardEntity;
import com.joo.digimon.card.model.JapaneseCardEntity;
import com.joo.digimon.card.repository.CardImgRepository;
import com.joo.digimon.card.repository.EnglishCardRepository;
import com.joo.digimon.card.repository.JapaneseCardRepository;
import com.joo.digimon.image.ImageUtil;
import com.joo.digimon.util.S3Util;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CardImageServiceImpl implements CardImageService {
    private final S3Util s3Util;
    private final CardImgRepository cardImgRepository;
    private final EnglishCardRepository englishCardRepository;
    private final JapaneseCardRepository japaneseCardRepository;

    private static final String KO_URL_PREFIX = "https://digimoncard.co.kr/";
    private static final String EN_URL_PREFIX = "https://world.digimoncard.com/";
    private static final String JP_URL_PREFIX = "https://digimoncard.com/";

    @Value("${img.original}")
    private String originalUploadPrefix;

    @Value("${img.small}")
    private String smallUploadPrefix;

    @Transactional
    @Override
    public int uploadAllImage() {
        int cnt = 0;
        cnt += uploadNotUploadYetEnCardImages();
        cnt += uploadNotUploadYetKorCardImages();
        cnt += uploadNotUploadYetJpCardImages();
        return cnt;
    }

    @Transactional
    public int uploadNotUploadYetKorCardImages() {
        List<CardImgEntity> cardImgEntityList = cardImgRepository.findByOriginUrlIsNotNullAndUploadUrlIsNull();
        cardImgEntityList.forEach(this::uploadImage);
        return cardImgEntityList.size();
    }

    @Transactional
    public int uploadNotUploadYetEnCardImages() {
        List<EnglishCardEntity> englishCardEntities = englishCardRepository.findByOriginUrlIsNotNullAndUploadUrlIsNull();
        englishCardEntities.forEach(this::uploadImageEn);
        return englishCardEntities.size();
    }

    @Transactional
    public int uploadNotUploadYetJpCardImages() {
        List<JapaneseCardEntity> japaneseCardEntities = japaneseCardRepository.findByOriginUrlIsNotNullAndUploadUrlIsNull();
        japaneseCardEntities.forEach(this::uploadImageJpn);
        return japaneseCardEntities.size();
    }

    @Override
    public int getUploadYetCount() {
        int cnt = 0;
        cnt += cardImgRepository.findByOriginUrlIsNotNullAndUploadUrlIsNull().size();
        cnt += englishCardRepository.findByOriginUrlIsNotNullAndUploadUrlIsNull().size();
        cnt += japaneseCardRepository.findByOriginUrlIsNotNullAndUploadUrlIsNull().size();
        return cnt;
    }

    private void uploadImage(CardImgEntity cardImgEntity) {
        try {
            BufferedImage image = ImageUtil.getImageData(KO_URL_PREFIX + cardImgEntity.getOriginUrl());
            BufferedImage compressedImage = Thumbnails.of(image)
                    .size(200, 280)
                    .asBufferedImage();
            StringBuilder keyNameBuilder = new StringBuilder();
            keyNameBuilder.append(cardImgEntity.getCardEntity().getCardNo());
            if (cardImgEntity.getIsParallel()) {
                keyNameBuilder.append("P").append(cardImgEntity.getId());
            }
            s3Util.uploadImageToS3(originalUploadPrefix + keyNameBuilder, image, "png");
            s3Util.uploadImageToS3(smallUploadPrefix + keyNameBuilder, compressedImage, "png");
            cardImgEntity.updateUploadUrl(originalUploadPrefix, smallUploadPrefix, keyNameBuilder.toString());
        } catch (Exception ignored) {
        }
    }

    private void uploadImageEn(EnglishCardEntity englishCardEntity) {
        try {
            BufferedImage image = ImageUtil.getImageData(EN_URL_PREFIX + englishCardEntity.getOriginUrl());
            StringBuilder keyNameBuilder = new StringBuilder();
            keyNameBuilder.append(englishCardEntity.getCardEntity().getCardNo());
            String uploadPrefix = originalUploadPrefix + "en/";
            s3Util.uploadImageToS3(uploadPrefix + keyNameBuilder, image, "png");
            englishCardEntity.updateUploadUrl(uploadPrefix, keyNameBuilder.toString());
        } catch (Exception ignored) {
        }
    }

    private void uploadImageJpn(JapaneseCardEntity japaneseCardEntity) {
        try {
            BufferedImage image = ImageUtil.getImageData(JP_URL_PREFIX + japaneseCardEntity.getOriginUrl());
            StringBuilder keyNameBuilder = new StringBuilder();
            keyNameBuilder.append(japaneseCardEntity.getCardEntity().getCardNo());
            String uploadPrefix = originalUploadPrefix + "jp/";
            s3Util.uploadImageToS3(uploadPrefix + keyNameBuilder, image, "png");
            japaneseCardEntity.updateUploadUrl(uploadPrefix, keyNameBuilder.toString());
        } catch (Exception ignored) {
        }
    }
    
}
