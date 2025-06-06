package com.joo.digimon.user.model;

import com.joo.digimon.global.enums.Locale;
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

    private Boolean isStrictDeck;

    @Column(columnDefinition = "TEXT")
    private String sortPriority;

    public void setLocalePriority(String localePriority) {
        this.localePriority = localePriority;
    }
    public void setDefaultLimitId(Integer defaultLimitId) {
        this.defaultLimitId = defaultLimitId;
    }
    public void setIsStrictDeck(Boolean isStrictDeck) {
        this.isStrictDeck = isStrictDeck;
    }

    public void setSortPriority(String sortPriority) {
        this.sortPriority = sortPriority;
    }
}
