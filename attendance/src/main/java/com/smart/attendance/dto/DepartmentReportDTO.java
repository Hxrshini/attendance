package com.smart.attendance.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DepartmentReportDTO {

    private long totalPresent;
    private long totalAbsent;

    private List<String> absenteesRollNumbers;
}