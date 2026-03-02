package com.smart.attendance.controller;
import com.smart.attendance.dto.AttendanceReportDTO;
import com.smart.attendance.dto.ManualEditDTO;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;   // ✅ CORRECT IMPORT
import org.springframework.web.bind.annotation.*;

import com.smart.attendance.dto.ManualEditDTO;
import com.smart.attendance.dto.StartSessionDTO;
import com.smart.attendance.service.FacultyService;
import com.smart.attendance.service.SessionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/faculty")
@RequiredArgsConstructor
public class FacultyController {

    private final SessionService sessionService;
    private final FacultyService facultyService;

  @PostMapping("/start-session")
public String startSession(@Valid @RequestBody StartSessionDTO dto,
                           Authentication authentication) {
    return sessionService.startSession(dto, authentication);
}

    @GetMapping("/session-report/{sessionId}")
    public List<AttendanceReportDTO> getSessionReport(@PathVariable Long sessionId,
                                   Authentication authentication) {
        return sessionService.getSessionReport(sessionId, authentication);
    }

    @PutMapping("/close-session/{sessionId}")
    public String closeSession(@PathVariable Long sessionId,
                               Authentication authentication) {
        return sessionService.closeSession(sessionId, authentication);
    }

    @PutMapping("/manual-edit")
public String manualEdit(@RequestBody ManualEditDTO dto,
                         Authentication authentication) {
    return sessionService.manualEdit(dto, authentication);
}
@GetMapping("/session-report/{sessionId}/download")
public ResponseEntity<byte[]> downloadSessionReport(
        @PathVariable Long sessionId,
        Authentication authentication) throws Exception {

    byte[] excelData =
            facultyService.generateSessionExcel(sessionId, authentication);

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=session-report.xlsx")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(excelData);
}
}