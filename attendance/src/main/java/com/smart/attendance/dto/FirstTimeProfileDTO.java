package com.smart.attendance.dto;

import lombok.Data;

@Data
public class FirstTimeProfileDTO {

    private String name;   // ✅ add this properly
    private String rollNo;
    private String profilePhotoPath;
    private String macAddress;
}