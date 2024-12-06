package com.joo.digimon.card.service;

import com.joo.digimon.card.model.CardImgEntity;
import com.joo.digimon.card.model.EnglishCardEntity;
import com.joo.digimon.card.model.JapaneseCardEntity;
import com.joo.digimon.card.repository.CardImgRepository;
import com.joo.digimon.card.repository.EnglishCardRepository;
import com.joo.digimon.card.repository.JapaneseCardRepository;
import com.joo.digimon.util.S3Util;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
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
        List<EnglishCardEntity> englishCardEntities = englishCardRepository.findByUploadUrlIsNull();

        englishCardEntities.forEach(this::uploadImageEn);

        return englishCardEntities.size();
    }

    @Transactional
    public int uploadNotUploadYetJpCardImages() {
        List<JapaneseCardEntity> japaneseCardEntities = japaneseCardRepository.findByUploadUrlIsNull();

        japaneseCardEntities.forEach(this::uploadImageJpn);

        return japaneseCardEntities.size();
    }

    @Override
    public int getUploadYetCount() {
        int cnt = 0;
        cnt += cardImgRepository.findByOriginUrlIsNotNullAndUploadUrlIsNull().size();
        cnt += englishCardRepository.findByUploadUrlIsNull().size();
        cnt += englishCardRepository.findByUploadUrlIsNull().size();
        return cnt;
    }

    private void uploadImage(CardImgEntity cardImgEntity) {
        try {
            BufferedImage image = getImageData(KO_URL_PREFIX + cardImgEntity.getOriginUrl());
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
            BufferedImage image = getImageData(EN_URL_PREFIX + englishCardEntity.getOriginUrl());
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
            BufferedImage image = getImageData(JP_URL_PREFIX + japaneseCardEntity.getOriginUrl());
            StringBuilder keyNameBuilder = new StringBuilder();
            keyNameBuilder.append(japaneseCardEntity.getCardEntity().getCardNo());
            String uploadPrefix = originalUploadPrefix + "jp/";
            s3Util.uploadImageToS3(uploadPrefix + keyNameBuilder, image, "png");
            japaneseCardEntity.updateUploadUrl(uploadPrefix, keyNameBuilder.toString());
        } catch (Exception ignored) {
        }
    }

    private BufferedImage getImageData(String imageUrl) throws IOException {
        BufferedImage image = ImageIO.read(new URL(imageUrl));
        return trimImageTransparent(image);
    }

    public BufferedImage trimImageTransparent(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int top = 0, left = 0, right = width - 1, bottom = height - 1;

        while (top < bottom) {
            int[] pixels = new int[width];
            image.getRGB(0, top, width, 1, pixels, 0, width);
            if (hasNonTransparentPixel(pixels)) {
                break;
            }
            top++;
        }

        while (bottom > top) {
            int[] pixels = new int[width];
            image.getRGB(0, bottom, width, 1, pixels, 0, width);
            if (hasNonTransparentPixel(pixels)) {
                break;
            }
            bottom--;
        }

        while (left < right) {
            int[] pixels = new int[height];
            image.getRGB(left, 0, 1, height, pixels, 0, 1);
            if (hasNonTransparentPixel(pixels)) {
                break;
            }
            left++;
        }

        while (right > left) {
            int[] pixels = new int[height];
            image.getRGB(right, 0, 1, height, pixels, 0, 1);
            if (hasNonTransparentPixel(pixels)) {
                break;
            }
            right--;
        }

        return image.getSubimage(left, top, right - left + 1, bottom - top + 1);
    }

    private boolean hasNonTransparentPixel(int[] pixels) {
        for (int pixel : pixels) {
            int alpha = (pixel >> 24) & 0xff;
            if (alpha != 0) {
                return true;
            }
        }
        return false;
    }
}
