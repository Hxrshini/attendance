package com.smart.attendance.service;

import com.smart.attendance.dto.*;

import java.util.List;

import org.springframework.security.core.Authentication;

public interface StudentService {



    String markAttendance(MarkAttendanceDTO dto, Authentication authentication);
String requestProfileEdit(Authentication authentication,
                          ProfileEditRequestDTO dto);


    String updateProfileFirstTime(Authentication authentication,
                              FirstTimeProfileDTO dto);
    Double getAttendancePercentage(Authentication authentication);

List<AttendanceReportDTO> getAttendanceHistory(Authentication authentication);

StudentProfileDTO getProfile(Authentication authentication);

StudentDashboardDTO getDashboard(Authentication authentication);
 
}