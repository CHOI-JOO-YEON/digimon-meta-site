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


import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Util {

    @Value("${s3.bucket-name}")
    String bucketName;

    private final RestTemplate restTemplate;

    private final S3Client s3Client;


    public boolean uploadImagePng(byte[] imageData, String keyName) {
        try {
            uploadToS3(keyName, imageData, "image/png");
        } catch (Exception e) {
            return false;
        }
        return true;

    }

    byte[] downloadImage(String imageUrl) throws IOException {
        Resource resource = restTemplate.getForObject(imageUrl, Resource.class);
        if (resource == null) {
            throw new IOException("Resource could not be downloaded from URL: " + imageUrl);
        }
        return resource.getInputStream().readAllBytes();
    }

    void uploadToS3(String keyName, byte[] data, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .contentType(contentType)
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(data));
    }


}
