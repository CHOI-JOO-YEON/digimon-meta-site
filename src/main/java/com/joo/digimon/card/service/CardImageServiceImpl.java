package com.joo.digimon.card.service;

import com.joo.digimon.card.dto.card.UploadWebpResponseDto;
import com.joo.digimon.card.model.CardImgEntity;
import com.joo.digimon.card.model.EnglishCardEntity;
import com.joo.digimon.card.model.JapaneseCardEntity;
import com.joo.digimon.card.repository.CardImgRepository;
import com.joo.digimon.card.repository.EnglishCardRepository;
import com.joo.digimon.card.repository.JapaneseCardRepository;
import com.joo.digimon.global.enums.Locale;
import com.joo.digimon.image.ImageUtil;
import com.joo.digimon.util.S3Util;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.sound.midi.Soundbank;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
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
    private static final String WEBP_PREFIX = "webp/";

    @Value("${domain.url}")
    private String prefixUrl;

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

    public void uploadImage(CardImgEntity cardImgEntity) {
        try {
            BufferedImage image = ImageUtil.getImageData(KO_URL_PREFIX + cardImgEntity.getOriginUrl());
            BufferedImage compressedImage = Thumbnails.of(image)
                    .size(200, 280)
                    .asBufferedImage();

            String key = generateImageKey(cardImgEntity.getCardEntity().getCardNo(), cardImgEntity.getIsParallel(), cardImgEntity.getId());

            uploadImage2(key, originalUploadPrefix, image);
            uploadImage2(key, smallUploadPrefix, compressedImage);

            cardImgEntity.updateUploadUrl(originalUploadPrefix, smallUploadPrefix, key);
            cardImgEntity.updateUploadWebpUrl(originalUploadPrefix, smallUploadPrefix, key, WEBP_PREFIX);
        } catch (Exception ignored) {
        }
    }

    private void uploadImage2(String key, String uploadPrefix, BufferedImage image) throws IOException {
        s3Util.uploadImageToS3(uploadPrefix + key, image, "png");
        s3Util.uploadImageToS3(WEBP_PREFIX + uploadPrefix + key, ImageUtil.convertBufferedImageToWebP(image), "webp");
    }

    private void uploadImageEn(EnglishCardEntity englishCardEntity) {
        try {
            BufferedImage image = ImageUtil.getImageData(EN_URL_PREFIX + englishCardEntity.getOriginUrl());
            String key = generateImageKey(englishCardEntity.getCardEntity().getCardNo(), false, null);
            String uploadPrefix = originalUploadPrefix + "en/";
            uploadImage2(key, uploadPrefix, image);
            englishCardEntity.updateUploadUrl(uploadPrefix, key);
            englishCardEntity.updateWebpUrl(uploadPrefix, key, WEBP_PREFIX);
        } catch (Exception ignored) {
        }
    }

    private void uploadImageJpn(JapaneseCardEntity japaneseCardEntity) {
        try {
            BufferedImage image = ImageUtil.getImageData(JP_URL_PREFIX + japaneseCardEntity.getOriginUrl());
            String key = generateImageKey(japaneseCardEntity.getCardEntity().getCardNo(), false, null);
            String uploadPrefix = originalUploadPrefix + "jp/";
            uploadImage2(key, uploadPrefix, image);
            japaneseCardEntity.updateUploadUrl(uploadPrefix, key);
            japaneseCardEntity.updateWebpUrl(uploadPrefix, key, WEBP_PREFIX);
        } catch (Exception ignored) {
        }
    }

    private static String generateImageKey(String cardNo, Boolean isParallel, Integer id) {
        StringBuilder keyNameBuilder = new StringBuilder();
        keyNameBuilder.append(cardNo);
        if (isParallel && id != null) {
            keyNameBuilder.append("P").append(id);
        }
        return keyNameBuilder.toString();
    }

    @Transactional
    public UploadWebpResponseDto uploadWebpImageYet(int count)
    {
        Instant startTime = Instant.now();
        List<CardImgEntity> imgEntities = cardImgRepository.findByBigWebpUrlIsNull(PageRequest.of(0, count));
        try {
            for (CardImgEntity imgEntity : imgEntities) {
                BufferedImage image = ImageUtil.getImageData(prefixUrl+imgEntity.getUploadUrl());
                String key = generateImageKey(imgEntity.getCardEntity().getCardNo(), imgEntity.getIsParallel(), imgEntity.getId());
                uploadWebpImage(image, key);
                imgEntity.updateUploadWebpUrl(originalUploadPrefix, smallUploadPrefix, key, WEBP_PREFIX);
            }
        }catch (Exception ignored) {
            ignored.printStackTrace();
        }

        cardImgRepository.saveAll(imgEntities);
        Instant endTime = Instant.now();
        long uploadDurationSeconds = Duration.between(startTime, endTime).getSeconds();

        int pendingCount = cardImgRepository.findByBigWebpUrlIsNull().size();
        return new UploadWebpResponseDto(imgEntities.size(), pendingCount, uploadDurationSeconds);
    }
    
    @Override
    public UploadWebpResponseDto uploadWebpImage(int count, Locale locale)
    {
        return switch (locale) {
            case Locale.KOR -> uploadWebpImageYet(count);
            case Locale.ENG -> uploadWebpImageEngYet(count);
            case Locale.JPN -> uploadWebpImageJpnYet(count);
        };
    }

    @Transactional
    public UploadWebpResponseDto uploadWebpImageEngYet(int count)
    {
        Instant startTime = Instant.now();
        List<EnglishCardEntity> englishCardEntities = englishCardRepository.findByWebpUrlIsNull(PageRequest.of(0, count));
        try {
            for (EnglishCardEntity englishCardEntity : englishCardEntities) {
                BufferedImage image = ImageUtil.getImageData(prefixUrl+englishCardEntity.getUploadUrl());
                String key = generateImageKey(englishCardEntity.getCardEntity().getCardNo(), false, null);
                byte[] webp = ImageUtil.convertBufferedImageToWebP(image);
                s3Util.uploadImageToS3(WEBP_PREFIX + originalUploadPrefix + "en/" + key, webp, "webp");

                englishCardEntity.updateWebpUrl(originalUploadPrefix + "en/", key, WEBP_PREFIX);
            }
        }catch (Exception ignored) {

        }
        englishCardRepository.saveAll(englishCardEntities);
        Instant endTime = Instant.now();
        long uploadDurationSeconds = Duration.between(startTime, endTime).getSeconds();

        int pendingCount = englishCardRepository.findByWebpUrlIsNull().size();
        return new UploadWebpResponseDto(englishCardEntities.size(), pendingCount, uploadDurationSeconds);
    }

    @Transactional
    public UploadWebpResponseDto uploadWebpImageJpnYet(int count)
    {
        Instant startTime = Instant.now();
        List<JapaneseCardEntity> japaneseCardEntities = japaneseCardRepository.findByWebpUrlIsNull(PageRequest.of(0, count));
        try {
            for (JapaneseCardEntity japaneseCardEntity : japaneseCardEntities) {
                BufferedImage image = ImageUtil.getImageData(prefixUrl+japaneseCardEntity.getUploadUrl());
                String key = generateImageKey(japaneseCardEntity.getCardEntity().getCardNo(), false, null);
                byte[] webp = ImageUtil.convertBufferedImageToWebP(image);
                s3Util.uploadImageToS3(WEBP_PREFIX + originalUploadPrefix + "jp/" + key, webp, "webp");
                japaneseCardEntity.updateWebpUrl(originalUploadPrefix + "jp/", key, WEBP_PREFIX);
            }
        }catch (Exception ignored) {

        }
        japaneseCardRepository.saveAll(japaneseCardEntities);
        Instant endTime = Instant.now();
        long uploadDurationSeconds = Duration.between(startTime, endTime).getSeconds();

        int pendingCount = japaneseCardRepository.findByWebpUrlIsNull().size();
        return new UploadWebpResponseDto(japaneseCardEntities.size(), pendingCount, uploadDurationSeconds);
    }
    
    public void uploadWebpImage(BufferedImage image, String key) throws IOException {
        BufferedImage compressedImage = Thumbnails.of(image)
                .size(200, 280)
                .asBufferedImage();

        byte[] big = ImageUtil.convertBufferedImageToWebP(image);
        byte[] small = ImageUtil.convertBufferedImageToWebP(compressedImage);

        s3Util.uploadImageToS3(WEBP_PREFIX + originalUploadPrefix + key, big, "webp");
        s3Util.uploadImageToS3(WEBP_PREFIX + smallUploadPrefix + key, small, "webp");
    }

}
