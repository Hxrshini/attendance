package com.smart.attendance.service;

import com.smart.attendance.dto.AttendanceReportDTO;
import com.smart.attendance.dto.DepartmentReportDTO;
import com.smart.attendance.entity.Attendance;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;

public interface HodService {

 


List<AttendanceReportDTO> getMonthlyReport();
List<AttendanceReportDTO> getWeeklyReport();
List<AttendanceReportDTO> getDailyReport();

    DepartmentReportDTO getDepartmentReport();

    List<com.smart.attendance.dto.AttendanceReportDTO> getAttendanceHistory(Long studentId);
    String assignTutor(Long facultyId, String classEmail);
    byte[] generateDailyReportExcel(Authentication authentication) throws Exception;
    @Query("""
    SELECT a FROM Attendance a
    WHERE a.markedAt >= :startDate
""")

byte[] generateWeeklyReportExcel(Authentication authentication) throws Exception;
byte[] generateMonthlyReportExcel(Authentication authentication) throws Exception;
}