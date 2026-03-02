package com.smart.attendance.service.impl;

import com.smart.attendance.service.ReportService;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl implements ReportService {

    @Override
    public String getSessionReport(Long sessionId) {
        return "Session report logic";
    }

    @Override
    public String getDepartmentReport() {
        return "Department report logic";
    }
}