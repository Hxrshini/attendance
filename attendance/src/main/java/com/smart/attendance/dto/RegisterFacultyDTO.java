package com.smart.attendance.dto;

import lombok.Data;


@Data
public class RegisterFacultyDTO {

    private String email;
    private String name;
    private String department;
    private boolean tutor;
    private String password;      // ✅ ADD THIS
    private String secretCode;
}