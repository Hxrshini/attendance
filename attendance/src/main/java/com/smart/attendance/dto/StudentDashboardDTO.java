package com.smart.attendance.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentDashboardDTO {

    private String name;
    private String rollNo;

    private long totalClasses;
    private long totalPresent;
    private long totalAbsent;

    private double percentage;

    private boolean markedToday;
}