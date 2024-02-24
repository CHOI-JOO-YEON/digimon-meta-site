package com.joo.digimon.user.model;

import com.joo.digimon.user.enums.AuthSupplier;
import com.joo.digimon.user.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Builder
@Table(name = "USERS_TB")
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;
    private String password;
    private String oauthId;
    private String nickName;

    @Enumerated(EnumType.STRING)
    private AuthSupplier authSupplier;
    @Enumerated(EnumType.STRING)
    private Role role;

    @CreationTimestamp
    private Timestamp createdDateTime;

    public String getUserIdentify() {
        if (this.authSupplier.equals(AuthSupplier.USERNAME)) {
            return username;
        }
        return oauthId;
    }
}
