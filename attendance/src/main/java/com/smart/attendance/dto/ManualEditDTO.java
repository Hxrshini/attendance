package com.smart.attendance.dto;

import lombok.Data;

@Data
public class ManualEditDTO {

    private Long sessionId;
    private Long studentId;
    private boolean present;
}