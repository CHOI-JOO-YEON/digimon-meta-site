package com.joo.digimon.user.controller;


import com.joo.digimon.global.annotation.argument_resolver.CurUser;
import com.joo.digimon.user.dto.UserSettingDto;
import com.joo.digimon.user.model.User;
import com.joo.digimon.user.service.UserSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserSettingService userSettingService;

    @GetMapping("/token/validate")
    ResponseEntity<?> isValidToken(){
       return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/setting")
    ResponseEntity<?> getSetting(@CurUser User user){
        return ResponseEntity.ok(userSettingService.getUserSetting(user));
    }

    @PutMapping("/setting")
    ResponseEntity<?> updateSetting(@CurUser User user, @RequestBody UserSettingDto dto){
        userSettingService.updateUserSetting(user, dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
