package com.smart.attendance.controller;

import com.smart.attendance.dto.DepartmentReportDTO;
import com.smart.attendance.service.HodService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hod")
@RequiredArgsConstructor
public class HodController {

    private final HodService hodService;

    // Summary (present, absent, absentees list)
    @GetMapping("/department-report")
    public DepartmentReportDTO getDepartmentReport() {
        return hodService.getDepartmentReport();
    }

    // Daily report
    @GetMapping("/report/daily")
    public Object getDailyReport() {
        return hodService.getDailyReport();
    }
    @GetMapping("/daily-report/download")
public ResponseEntity<byte[]> downloadDailyReport(Authentication authentication) throws Exception {

    byte[] excelData = hodService.generateDailyReportExcel(authentication);

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=daily-report.xlsx")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(excelData);
}
    // Weekly report
    @GetMapping("/report/weekly")
    public Object getWeeklyReport() {
        return hodService.getWeeklyReport();
    }

    // Monthly report
    @GetMapping("/report/monthly")
    public Object getMonthlyReport() {
        return hodService.getMonthlyReport();
    }

@PutMapping("/assign-tutor")
public ResponseEntity<String> assignTutor(
        @RequestParam Long facultyId,
        @RequestParam String classEmail) {

    return ResponseEntity.ok(
            hodService.assignTutor(facultyId, classEmail)
    );
}
@GetMapping("/weekly-report/download")
public ResponseEntity<byte[]> downloadWeeklyReport(Authentication authentication) throws Exception {

    byte[] excelData = hodService.generateWeeklyReportExcel(authentication);

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=weekly-report.xlsx")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(excelData);
}
@GetMapping("/monthly-report/download")
public ResponseEntity<byte[]> downloadMonthlyReport(Authentication authentication) throws Exception {

    byte[] excelData = hodService.generateMonthlyReportExcel(authentication);

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=monthly-report.xlsx")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(excelData);
}
}