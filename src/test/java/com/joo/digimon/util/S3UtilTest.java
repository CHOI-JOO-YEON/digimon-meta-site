package com.joo.digimon.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
class S3UtilTest {

    @Autowired
    private S3Util s3Util;
    @Test
    void uploadImageSuccessTest() {
//        s3Util.uploadImagePng("https://digimoncard.co.kr/files/attach/images/139/841/009/3673d69253aef78bec62a793d2c557cb.png","/card/bt7-001");
    }
}