package com.smart.attendance.service;

public interface ReportService {

    String getSessionReport(Long sessionId);

    String getDepartmentReport();
}