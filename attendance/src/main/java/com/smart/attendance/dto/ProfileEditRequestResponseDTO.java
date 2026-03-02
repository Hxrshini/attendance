package com.smart.attendance.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileEditRequestResponseDTO {

    private Long requestId;
    private String studentName;
    private String rollNo;
    private String requestedField;
    private String newValue;
    private String status;
}