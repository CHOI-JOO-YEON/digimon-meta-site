package com.joo.digimon.report.service;

import com.joo.digimon.global.exception.model.ForbiddenAccessException;
import com.joo.digimon.global.exception.model.ReportException;
import com.joo.digimon.report.dto.CreateReportRequest;
import com.joo.digimon.report.dto.ReportResponse;
import com.joo.digimon.report.enums.ReportStatus;
import com.joo.digimon.report.model.Report;
import com.joo.digimon.report.repository.ReportRepository;
import com.joo.digimon.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    @Override
    public void createReport(User user, CreateReportRequest createReportRequest) {
        reportRepository.save(
                Report.builder()
                        .text(createReportRequest.getText())
                        .category(createReportRequest.getReportCategory())
                        .status(ReportStatus.IN_PROGRESS)
                        .title(createReportRequest.getTitle())
                        .user(user)
                        .build()
        );
    }

    @Override
    public void deleteReport(User user, Integer reportRequestId) {
        Report report = reportRepository.findById(reportRequestId).orElseThrow();
        if (!report.getUser().equals(user)) {
            throw new ForbiddenAccessException();
        }
        if (!report.getStatus().equals(ReportStatus.SUBMITTED)) {
            throw new ReportException("삭제할 수 없는 상태입니다.");
        }
        reportRepository.delete(report);

    }

    @Override
    public List<ReportResponse> getAllReport() {
        List<Report> allReports = reportRepository.findAll();
        List<ReportResponse> reportResponseList = new ArrayList<>();
        for (Report report : allReports) {
            reportResponseList.add(new ReportResponse(report));
        }
        return reportResponseList;
    }

    @Override
    public List<ReportResponse> getMyReport(User user) {
        List<Report> allReports = reportRepository.findByUser(user);
        List<ReportResponse> reportResponseList = new ArrayList<>();
        for (Report report : allReports) {
            reportResponseList.add(new ReportResponse(report));
        }
        return reportResponseList;
    }
}
