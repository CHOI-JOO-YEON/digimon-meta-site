package com.joo.digimon.report.service;


import com.joo.digimon.report.dto.CreateReportRequest;
import com.joo.digimon.report.dto.ReportResponse;
import com.joo.digimon.user.model.User;

import java.util.List;

public interface ReportService {
    void createReport(User user, CreateReportRequest createReportRequest);
    void deleteReport(User user, Integer reportRequestId);
    List<ReportResponse> getAllReport();
    List<ReportResponse> getMyReport(User user);

}
