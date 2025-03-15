package com.example.mobile.controller;

import com.example.mobile.dto.request.ReportDto;
import com.example.mobile.dto.response.ApiResponse;
import com.example.mobile.model.Report;
import com.example.mobile.service.IReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    @Autowired
    private IReportService reportService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Report>> sendReport(@RequestBody ReportDto reportDto) {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(),"success",reportService.sendReport(reportDto)));
    }

}
