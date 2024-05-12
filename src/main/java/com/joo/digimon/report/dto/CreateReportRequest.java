package com.joo.digimon.report.dto;

import com.joo.digimon.report.enums.ReportCategory;
import lombok.Data;

@Data
public class CreateReportRequest {
    ReportCategory reportCategory;
    String title;
    String text;
}
