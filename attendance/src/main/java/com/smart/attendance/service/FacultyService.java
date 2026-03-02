package com.smart.attendance.service;

import com.smart.attendance.entity.AttendanceSession;

import java.util.List;

import org.springframework.security.core.Authentication;

public interface FacultyService {

    AttendanceSession startSession(String classEmail,
                                    String subject,
                                    String period,
                                    double latitude,
                                    double longitude,
                                    double radius,
                                    int durationMinutes,
                                    Long facultyId);

    String closeSession(Long sessionId);

    String manualEditAttendance(Long attendanceId, boolean present);

    List<AttendanceSession> getActiveSessionsByFaculty(Long facultyId);

  
    byte[] generateSessionExcel(Long sessionId, Authentication authentication) throws Exception;
    byte[] downloadActiveSessionReport(Authentication authentication) throws Exception;
}