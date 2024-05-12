package com.joo.digimon.report.dto;

import com.joo.digimon.report.enums.ReportCategory;
import com.joo.digimon.report.enums.ReportStatus;
import com.joo.digimon.report.model.Report;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class ReportResponse {
    private ReportCategory reportCategory;
    private String title;
    private String text;
    private ReportStatus reportStatus;
    private String userName;
    private Integer userId;
    private String operatorResponse;
    private Timestamp createTime;

    public ReportResponse(Report report) {
        this.reportCategory = report.getCategory();
        this.title = report.getTitle();
        this.text = report.getText();
        this.reportStatus = report.getStatus();
        this.userName = report.getUser().getNickName();
        this.userId = report.getUser().getId();
        this.operatorResponse = report.getOperatorResponse();
        this.createTime = report.getCreatedDateTime();
    }
}
