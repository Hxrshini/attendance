package com.smart.attendance.mapper;

import com.smart.attendance.dto.ProfileEditRequestDTO;
import com.smart.attendance.entity.ProfileEditRequest;
import com.smart.attendance.entity.Student;

import java.time.LocalDateTime;

public class ProfileMapper {

    public static ProfileEditRequest toEntity(ProfileEditRequestDTO dto,
                                              Student student) {

        return ProfileEditRequest.builder()
                .student(student)
                .requestedField(dto.getField())
                .newValue(dto.getNewValue())
                .status("PENDING")
                .requestedAt(LocalDateTime.now())
                .build();
    }
}