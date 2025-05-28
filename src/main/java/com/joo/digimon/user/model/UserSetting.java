package com.joo.digimon.user.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "USER_SETTINGS_TB")
public class UserSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "users_tb_id", unique = true)
    private User user;

    // comma separated locale order e.g. "KOR,ENG,JPN"
    private String localePriority;

    private Integer defaultLimitId;

    // comma separated sort priority keys
    private String sortPriority;

    private Boolean isStrictDeck;

    public void setLocalePriority(String localePriority) {
        this.localePriority = localePriority;
    }
    public void setDefaultLimitId(Integer defaultLimitId) {
        this.defaultLimitId = defaultLimitId;
    }
    public void setSortPriority(String sortPriority) {
        this.sortPriority = sortPriority;
    }
    public void setIsStrictDeck(Boolean isStrictDeck) {
        this.isStrictDeck = isStrictDeck;
    }
}
