package com.joo.digimon.collect.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserCardId implements Serializable {
    private Integer user;
    private Integer cardImgEntity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserCardId that = (UserCardId) o;
        return Objects.equals(user, that.user) && Objects.equals(cardImgEntity, that.cardImgEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, cardImgEntity);
    }
}