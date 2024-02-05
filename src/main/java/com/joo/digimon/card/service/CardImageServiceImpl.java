package com.joo.digimon.card.service;

import com.joo.digimon.card.externel.ScalingClient;
import com.joo.digimon.card.model.CardImgEntity;
import com.joo.digimon.card.model.ParallelCardImgEntity;
import com.joo.digimon.card.repository.CardImgRepository;
import com.joo.digimon.card.repository.ParallelCardImgRepository;
import com.joo.digimon.util.S3Util;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardImageServiceImpl implements CardImageService {
    private final S3Util s3Util;
    private final CardImgRepository cardImgRepository;
    private final ParallelCardImgRepository parallelCardImgRepository;
    private final ScalingClient scalingClient;
    private static final String KO_URL_PREFIX = "https://digimoncard.co.kr/";

    @Value("${img.prefix}")
    private String UPLOAD_PREFIX;

    private final static Integer MIN_WIDTH = 600;
    private final static Integer MIN_HEIGHT = 900;



    @Override
    @Transactional
    public int uploadNotUploadYetCardImages() {
        int cnt = 0;

        List<CardImgEntity> cardImgEntityList = cardImgRepository.findByUploadUrlIsNull();

        for (CardImgEntity cardImgEntity : cardImgEntityList) {
            StringBuilder keyNameBuilder = new StringBuilder();
            keyNameBuilder.append(UPLOAD_PREFIX).append(cardImgEntity.getCardEntity().getCardNo());
            if (cardImgEntity.getIsParallel()) {
                keyNameBuilder.append("P").append(cardImgEntity.getId());
            }


            try {
                if (s3Util.uploadImagePng(getImageData(KO_URL_PREFIX + cardImgEntity.getOriginUrl()), keyNameBuilder.toString())) {
                    cardImgEntity.updateUploadUrl( keyNameBuilder.toString());
                    cnt++;
                }
            } catch (IOException ignore) {
            }
        }

        return cnt;
    }

    private byte[] getImageData(String imageUrl) throws IOException {
        BufferedImage image = ImageIO.read(new URL(imageUrl));

        if (image.getWidth() < MIN_WIDTH || image.getHeight() < MIN_HEIGHT) {
            byte[] scaledImageData = scaleImageUsingWaifu2x(imageUrl);
            if (scaledImageData != null) {
                image = ImageIO.read(new ByteArrayInputStream(scaledImageData));
            }
        }
        BufferedImage trimmedImage = trimImageTransparent(image);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(trimmedImage, "png", os);
        return os.toByteArray();
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

    private byte[] scaleImageUsingWaifu2x(String imageUrl){
        return scalingClient.upscaleImage(imageUrl, 2, 3, "art");

    }
}
