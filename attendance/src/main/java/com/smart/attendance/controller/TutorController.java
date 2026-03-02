package com.smart.attendance.controller;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.smart.attendance.dto.AttendanceReportDTO;
import com.smart.attendance.dto.ProfileEditRequestResponseDTO;
import com.smart.attendance.entity.AuditLog;
import com.smart.attendance.entity.DeviceChangeLog;
import com.smart.attendance.entity.ProfileEditRequest;
import com.smart.attendance.service.TutorService;

@RestController
@RequestMapping("/api/tutor")
@RequiredArgsConstructor
public class TutorController {
     private final TutorService tutorService;
    
    @GetMapping("/class-report")
public ResponseEntity<List<AttendanceReportDTO>> getClassReport(
        Authentication authentication) {

    return ResponseEntity.ok(
            tutorService.getClassReport(authentication)
    );
}
  @GetMapping("/pending-requests")
public ResponseEntity<List<ProfileEditRequestResponseDTO>> getPendingRequests(
        Authentication authentication) {

    return ResponseEntity.ok(
            tutorService.getPendingRequests(authentication)
    );
}
 // ✅ Approve profile
    @PutMapping("/approve-profile/{requestId}")
    public ResponseEntity<String> approveProfile(
            @PathVariable Long requestId,
            Authentication authentication) {

        return ResponseEntity.ok(
                tutorService.approveProfileEdit(requestId, authentication)
        );
    }

    // ✅ Reject profile
    @PutMapping("/reject-profile/{requestId}")
    public ResponseEntity<String> rejectProfile(
            @PathVariable Long requestId,
            Authentication authentication) {

        return ResponseEntity.ok(
                tutorService.rejectProfileEdit(requestId, authentication)
        );
    }

    // ✅ Suspicious logs
    @GetMapping("/suspicious-logs")
    public ResponseEntity<List<AuditLog>> getLogs(
            Authentication authentication) {

        return ResponseEntity.ok(
                tutorService.getSuspiciousLogs(authentication)
        );
    }

    // ✅ Device history
    @GetMapping("/device-history")
    public ResponseEntity<List<DeviceChangeLog>> getDeviceHistory(
            Authentication authentication) {

        return ResponseEntity.ok(
                tutorService.getDeviceChangeHistory(authentication)
        );
    }
    @GetMapping("/class-report/download")
public ResponseEntity<byte[]> downloadClassReport(Authentication authentication) throws Exception {

    byte[] excelData =
            tutorService.downloadClassReportExcel(authentication);

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=class-report.xlsx")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(excelData);
}
}