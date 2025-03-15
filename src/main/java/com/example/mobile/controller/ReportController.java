package com.example.mobile.controller;

import com.example.mobile.dto.request.ReportDto;
import com.example.mobile.dto.response.ApiResponse;
import com.example.mobile.model.Report;
import com.example.mobile.service.IReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "4. Report", description = "Các API liên quan đến các báo cáo")
public class ReportController {
    @Autowired
    private IReportService reportService;

    @Operation(summary = "Báo cáo người dùng", description = "Báo cáo một người dùng với lý do tự chọn")
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Report>> sendReport(@RequestBody ReportDto reportDto) {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(),"Thành công",reportService.sendReport(reportDto)));
    }

}
