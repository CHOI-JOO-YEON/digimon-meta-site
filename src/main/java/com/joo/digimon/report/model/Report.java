package com.joo.digimon.report.model;

import com.joo.digimon.report.enums.ReportCategory;
import com.joo.digimon.report.enums.ReportStatus;
import com.joo.digimon.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@NamedEntityGraph(
        name = "Report.detail",
        attributeNodes = {@NamedAttributeNode(value = "user"),}
)
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "users_tb_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private ReportCategory category;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String text;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @Column(columnDefinition = "TEXT")
    private String operatorResponse;

    @CreationTimestamp
    private Timestamp createdDateTime;
}
