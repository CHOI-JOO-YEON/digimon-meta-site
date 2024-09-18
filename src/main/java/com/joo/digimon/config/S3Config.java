package com.joo.digimon.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class S3Config {

    @Value("${aws.access-key-id}")
    private String accessKeyId;
    @Value("${aws.access-key-secret}")
    private String accessKeySecret;

//    @Bean
//    S3Client s3Client(){
//        return S3Client.builder()
//                .region(Region.AP_NORTHEAST_2)
//                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, accessKeySecret)))
//                .build();
//    }

    @Bean
    @Profile("dev")
    S3Client localS3Client(@Value("${aws.s3.endpoint}") String s3Endpoint) {
        return S3Client.builder()
                .region(Region.AP_NORTHEAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, accessKeySecret)))
                .endpointOverride(URI.create(s3Endpoint))
                .build();
    }

    @Bean
    @Profile("prod-ec2")
    S3Client prodS3Client() {
        return S3Client.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, accessKeySecret)))
                .build();
    }

}
