package com.joo.digimon.card.service;

import com.joo.digimon.card.externel.ScalingClient;
import com.joo.digimon.card.model.CardImgEntity;
import com.joo.digimon.card.repository.CardImgRepository;
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
    private final ScalingClient scalingClient;
    private static final String KO_URL_PREFIX = "https://digimoncard.co.kr/";
    private static final String EN_URL_PREFIX = "https://world.digimoncard.com/";

    @Value("${img.original}")
    private String originalUploadPrefix;

    @Value("${img.small}")
    private String smallUploadPrefix;

    private final static Integer MIN_WIDTH = 600;
    private final static Integer MIN_HEIGHT = 900;

    @Override
    @Transactional
    public int uploadNotUploadYetKorCardImages() {
        int cnt = 0;

        List<CardImgEntity> cardImgEntityList = cardImgRepository.findByUploadUrlIsNullAndIsEnCardIsNull();

        for (CardImgEntity cardImgEntity : cardImgEntityList) {
            try {
                uploadImage(cardImgEntity, KO_URL_PREFIX);
                cnt++;
            } catch (IOException ignore) {

            }

        }

        return cnt;
    }

    @Transactional
    @Override
    public int uploadAllImage() {
        int cnt = 0;

        List<CardImgEntity> cardImgEntityList = cardImgRepository.findByUploadUrlIsNullAndIsEnCardIsNull();

        for (CardImgEntity cardImgEntity : cardImgEntityList) {
            try {
                uploadImage(cardImgEntity, KO_URL_PREFIX);
                cnt++;
            } catch (IOException ignore) {

            }

        }

        return cnt;
    }

    @Override
    @Transactional
    public int uploadNotUploadYetEnCardImages() {
        int cnt = 0;

        List<CardImgEntity> cardImgEntityList = cardImgRepository.findByUploadUrlIsNullAndIsEnCardTrue();

        for (CardImgEntity cardImgEntity : cardImgEntityList) {
            try {
                uploadImageEn(cardImgEntity, EN_URL_PREFIX);
                cnt++;
            } catch (IOException ignore) {

            }

        }

        return cnt;
    }

    @Override
    public int getUploadYetCount() {
        return cardImgRepository.findByUploadUrlIsNullAndIsEnCardIsNull().size();
    }

    private void uploadImage(CardImgEntity cardImgEntity, String urlPrefix) throws IOException {

        BufferedImage image = getImageData(urlPrefix + cardImgEntity.getOriginUrl());
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
        cardImgEntity.updateUploadUrl(originalUploadPrefix,smallUploadPrefix,keyNameBuilder.toString());
    }

    private void uploadImageEn(CardImgEntity cardImgEntity, String urlPrefix) throws IOException {

        BufferedImage image = getImageData(urlPrefix + cardImgEntity.getOriginUrl());

        StringBuilder keyNameBuilder = new StringBuilder();
        keyNameBuilder.append(cardImgEntity.getCardEntity().getCardNo());
        if (cardImgEntity.getIsParallel()) {
            keyNameBuilder.append("P").append(cardImgEntity.getId());
        }
        String uploadPrefix = originalUploadPrefix+"en/";
        s3Util.uploadImageToS3(uploadPrefix + keyNameBuilder, image, "png");
        cardImgEntity.updateUploadUrl(uploadPrefix,uploadPrefix,keyNameBuilder.toString());
    }

    private BufferedImage getImageData(String imageUrl) throws IOException {
        BufferedImage image = ImageIO.read(new URL(imageUrl));

//        if (image.getWidth() < MIN_WIDTH || image.getHeight() < MIN_HEIGHT) {
//            byte[] scaledImageData = scaleImageUsingWaifu2x(imageUrl);
//            if (scaledImageData != null) {
//                image = ImageIO.read(new ByteArrayInputStream(scaledImageData));
//            }
//        }
        BufferedImage trimmedImage = trimImageTransparent(image);
        return trimmedImage;
    }

    public BufferedImage trimImageTransparent(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int top = 0, left = 0, right = width - 1, bottom = height - 1;

        // Find top
        while (top < bottom) {
            int[] pixels = new int[width];
            image.getRGB(0, top, width, 1, pixels, 0, width);
            if (hasNonTransparentPixel(pixels)) {
                break;
            }
            top++;
        }

        // Find bottom
        while (bottom > top) {
            int[] pixels = new int[width];
            image.getRGB(0, bottom, width, 1, pixels, 0, width);
            if (hasNonTransparentPixel(pixels)) {
                break;
            }
            bottom--;
        }

        // Find left
        while (left < right) {
            int[] pixels = new int[height];
            image.getRGB(left, 0, 1, height, pixels, 0, 1);
            if (hasNonTransparentPixel(pixels)) {
                break;
            }
            left++;
        }

        // Find right
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

    private byte[] scaleImageUsingWaifu2x(String imageUrl) {
        return scalingClient.upscaleImage(imageUrl, 2, 3, "art");

    }
}
