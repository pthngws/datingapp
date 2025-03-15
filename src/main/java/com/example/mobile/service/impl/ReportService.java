package com.example.mobile.service.impl;

import com.example.mobile.dto.request.ReportDto;
import com.example.mobile.model.Report;
import com.example.mobile.repository.ReportRepository;
import com.example.mobile.service.IReportService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ReportService implements IReportService {
    @Autowired
    private ReportRepository reportRepository;

    @Override
    public Report sendReport(ReportDto reportDto){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        ObjectId userId = new ObjectId(authentication.getName());
        Report report = new Report();
        report.setReporterId(userId);
        report.setReportedUserId(reportDto.getReportedUserId());
        report.setReason(reportDto.getReason());
        return reportRepository.save(report);
    }
}
