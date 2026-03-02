package com.smart.attendance.dto;

import lombok.Data;

@Data
public class ProfileEditRequestDTO {

    private Long studentId;
    private String field;
    private String newValue;
}