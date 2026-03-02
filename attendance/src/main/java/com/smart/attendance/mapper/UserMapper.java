package com.smart.attendance.mapper;

import com.smart.attendance.dto.*;
import com.smart.attendance.entity.*;

public class UserMapper {

    public static User toStudentUser(RegisterStudentDTO dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(Role.STUDENT)
                .active(true)
                .build();
    }

    public static Student toStudentEntity(RegisterStudentDTO dto, User user) {
        return Student.builder()
                .user(user)
                .rollNo(dto.getRollNo())
                .classEmail(dto.getClassEmail())
                .build();
    }

   
    public static User toFacultyUser(RegisterFacultyDTO dto,
                                  String encodedPassword) {

    return User.builder()
            .name(dto.getName())
            .email(dto.getEmail())
            .password(encodedPassword)   // already encoded
            .role(Role.FACULTY)
            .active(true)
            .firstLogin(true)
            .build();
}

    public static Faculty toFacultyEntity(RegisterFacultyDTO dto, User user) {
        return Faculty.builder()
                .user(user)
                .department(dto.getDepartment())
                .isTutor(dto.isTutor())
                .build();
    }
}