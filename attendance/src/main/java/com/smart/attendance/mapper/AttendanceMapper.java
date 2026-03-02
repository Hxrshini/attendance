package com.smart.attendance.mapper;

import com.smart.attendance.dto.MarkAttendanceDTO;
import com.smart.attendance.entity.*;

import java.time.LocalDateTime;

public class AttendanceMapper {

    public static Attendance toEntity(Student student,
                                      AttendanceSession session,
                                      MarkAttendanceDTO dto) {

        return Attendance.builder()
                .student(student)
                .session(session)
                .present(true)
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .selfiePath(dto.getSelfiePath())
                .markedAt(LocalDateTime.now())
                .build();
    }
}