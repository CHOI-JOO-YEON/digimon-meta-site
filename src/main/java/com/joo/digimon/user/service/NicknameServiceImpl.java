package com.joo.digimon.user.service;

import org.springframework.stereotype.Service;

@Service
public class NicknameServiceImpl implements NicknameService{
    @Override
    public String generateNickname() {
        return "사용자";
    }
}
