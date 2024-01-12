package com.joo.digimon.user.dto;

import com.joo.digimon.user.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    String accessToken;
    String nickname;
    Role role;
}
