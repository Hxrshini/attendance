package com.smart.attendance.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AttendanceReportDTO {

    private String rollNo;

    private String studentName;

    private String subject;

    private boolean present;

    private LocalDateTime markedAt;
}