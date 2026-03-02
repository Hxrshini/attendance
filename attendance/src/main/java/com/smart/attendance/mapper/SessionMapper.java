package com.smart.attendance.mapper;

import com.smart.attendance.dto.StartSessionDTO;
import com.smart.attendance.entity.AttendanceSession;

import java.time.LocalDateTime;
import java.util.UUID;

public class SessionMapper {

    public static AttendanceSession toEntity(StartSessionDTO dto) {
        return AttendanceSession.builder()
                .classEmail(dto.getClassEmail())
                .subject(dto.getSubject())
                .period(dto.getPeriod())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .radius(dto.getRadius())
                .qrToken(UUID.randomUUID().toString())
                .startTime(LocalDateTime.now())
                .active(true)
                .build();
    }
}