package com.joo.digimon.report.controller;

import com.joo.digimon.global.annotation.argument_resolver.CurUser;
import com.joo.digimon.report.dto.CreateReportRequest;
import com.joo.digimon.report.service.ReportService;
import com.joo.digimon.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    @PostMapping("/")
    ResponseEntity<?> createReport(@CurUser User user, CreateReportRequest createReportRequest) {
        reportService.createReport(user,createReportRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/")
    ResponseEntity<?> getUserReport(@CurUser User user) {
        return new ResponseEntity<>(reportService.getMyReport(user), HttpStatus.OK);
    }
}
