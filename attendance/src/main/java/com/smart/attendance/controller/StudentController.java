package com.smart.attendance.controller;

import org.springframework.security.core.Authentication;
import com.smart.attendance.dto.*;
import com.smart.attendance.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    // ✅ Mark Attendance
    @PostMapping("/mark-attendance")
    public ResponseEntity<String> markAttendance(
            @RequestBody MarkAttendanceDTO dto,
            Authentication authentication) {

        return ResponseEntity.ok(
                studentService.markAttendance(dto, authentication)
        );
    }

    // ✅ Attendance History
    @GetMapping("/history")
    public ResponseEntity<List<AttendanceReportDTO>> getHistory(
            Authentication authentication) {

        return ResponseEntity.ok(
                studentService.getAttendanceHistory(authentication)
        );
    }

    // ✅ Attendance Percentage
    @GetMapping("/percentage")
    public ResponseEntity<Double> getPercentage(
            Authentication authentication) {

        return ResponseEntity.ok(
                studentService.getAttendancePercentage(authentication)
        );
    }

    // ✅ Get Profile
    @GetMapping("/profile")
    public ResponseEntity<StudentProfileDTO> getProfile(
            Authentication authentication) {

        return ResponseEntity.ok(
                studentService.getProfile(authentication)
        );
    }

    // ✅ Dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<StudentDashboardDTO> getDashboard(
            Authentication authentication) {

        return ResponseEntity.ok(
                studentService.getDashboard(authentication)
        );
    }

    // ✅ First Time Profile Setup
    @PutMapping("/set-profile")
    public ResponseEntity<String> setProfileFirstTime(
            Authentication authentication,
            @RequestBody FirstTimeProfileDTO dto) {

        return ResponseEntity.ok(
                studentService.updateProfileFirstTime(authentication, dto)
        );
    }

    // ✅ Request Profile Edit
    @PostMapping("/request-profile-edit")
    public ResponseEntity<String> requestProfileEdit(
            Authentication authentication,
            @RequestBody ProfileEditRequestDTO dto) {

        return ResponseEntity.ok(
                studentService.requestProfileEdit(authentication, dto)
        );
    }
}