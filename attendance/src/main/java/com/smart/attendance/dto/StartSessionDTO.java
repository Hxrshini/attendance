package com.smart.attendance.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class StartSessionDTO {

    @NotBlank
    private String classEmail;

    @NotBlank
    private String subject;

    @NotBlank
    private String period;

    @Positive
    private double latitude;

    @Positive
    private double longitude;

    @Positive
    private double radius;

    @Min(1)
    @Max(15)
    private Integer durationMinutes;  // 1–15 minutes allowed
}