package com.smart.attendance.service;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.smart.attendance.dto.AttendanceReportDTO;
import com.smart.attendance.dto.ProfileEditRequestResponseDTO;
import com.smart.attendance.entity.AuditLog;
import com.smart.attendance.entity.DeviceChangeLog;
import com.smart.attendance.entity.ProfileEditRequest;

public interface TutorService {



   List<AttendanceReportDTO> getClassReport(Authentication authentication);
 
  
    String resetStudentMac(Long tutorId,
                       Long studentId,
                       String newMac);

 public List<ProfileEditRequestResponseDTO> getPendingRequests(Authentication authentication);

    String approveProfileEdit(Long requestId, Authentication authentication);

    String rejectProfileEdit(Long requestId, Authentication authentication);

    List<AuditLog> getSuspiciousLogs(Authentication authentication);

    List<DeviceChangeLog> getDeviceChangeHistory(Authentication authentication);

   byte[] downloadClassReportExcel(Authentication authentication) throws Exception;

}