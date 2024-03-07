package com.joo.digimon.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import org.springframework.core.io.Resource;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Util {

    @Value("${s3.bucket-name}")
    String bucketName;

    private final RestTemplate restTemplate;

    private final S3Client s3Client;


    public void uploadImageToS3(String keyName, BufferedImage image, String fileType) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .contentType("image/"+fileType)
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(convertBufferedImageToByteArray(image, fileType)));
    }

    byte[] convertBufferedImageToByteArray(BufferedImage image, String formatName) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, formatName, baos);
            return baos.toByteArray();
        }
    }
}
