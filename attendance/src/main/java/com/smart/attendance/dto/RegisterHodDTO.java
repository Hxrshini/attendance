package com.smart.attendance.dto;

import lombok.Data;

@Data
public class RegisterHodDTO {

    private String name;
    private String email;
    private String password;      // ✅ ADD THIS
    private String secretCode;
}