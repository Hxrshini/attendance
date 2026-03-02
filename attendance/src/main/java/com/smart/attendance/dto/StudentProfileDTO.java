package com.smart.attendance.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentProfileDTO {

    private Long studentId;

    private String name;

    private String email;

    private String rollNo;

    private String classEmail;

    private String department;

    private String tutorName;

    private String profilePhotoPath;

    private boolean active;
}