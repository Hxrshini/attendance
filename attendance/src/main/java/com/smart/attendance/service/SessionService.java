package com.smart.attendance.service;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.smart.attendance.dto.AttendanceReportDTO;
import com.smart.attendance.dto.ManualEditDTO;
import com.smart.attendance.dto.StartSessionDTO;

public interface SessionService {

 

    String closeSession(Long sessionId, Authentication authentication);
    List<AttendanceReportDTO> getSessionReport(Long sessionId, Authentication authentication);
 
    String manualEdit(ManualEditDTO dto, Authentication authentication);
  
    String startSession(StartSessionDTO dto, Authentication authentication);
    
}