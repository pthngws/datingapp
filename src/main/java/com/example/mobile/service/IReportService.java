package com.example.mobile.service;

import com.example.mobile.dto.request.ReportDto;
import com.example.mobile.model.Report;

public interface IReportService {
    Report sendReport(ReportDto reportDto);
}
