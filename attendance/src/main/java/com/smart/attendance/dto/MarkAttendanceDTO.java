package com.smart.attendance.dto;

import lombok.Data;

@Data
public class MarkAttendanceDTO {

  
    private String qrToken;
    private double latitude;
    private double longitude;
    private String selfiePath;
   
}