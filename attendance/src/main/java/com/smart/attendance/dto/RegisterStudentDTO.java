package com.smart.attendance.dto;

import lombok.Data;

@Data
public class RegisterStudentDTO {

    private String name;
    private String email;
    private String password;
    private String rollNo;
    private String classEmail;
}