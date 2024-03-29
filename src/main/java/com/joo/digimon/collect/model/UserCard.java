package com.joo.digimon.collect.model;

import com.joo.digimon.card.model.CardImgEntity;
import com.joo.digimon.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@IdClass(UserCardId.class)
@NamedEntityGraph(
        name = "UserCard.detail", attributeNodes = {
        @NamedAttributeNode(value = "cardImgEntity", subgraph = "cardImgEntitySubGraph")
},
        subgraphs = {
                @NamedSubgraph(
                        name = "cardImgEntitySubGraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "cardEntity"),
                        }
                ),

        }
)
public class UserCard {
    @Id
    @ManyToOne
    @JoinColumn(name = "users_tb_id")
    User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "cards_img_tb_id")
    CardImgEntity cardImgEntity;

    Integer quantity;
}

