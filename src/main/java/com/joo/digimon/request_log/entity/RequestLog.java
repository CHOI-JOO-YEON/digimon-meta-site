package com.joo.digimon.request_log.entity;

import com.joo.digimon.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import net.sf.uadetector.ReadableUserAgent;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Builder
@Table(name = "REQUEST_LOGS_TB")
@ToString
public class RequestLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "users_tb_id")
    User user;

    Long executionTime;

    String requestPath;
    String requestMethod;

    String deviceCategory;
    String type;
    String family;

    @CreationTimestamp
    private Timestamp createdDateTime;
}
