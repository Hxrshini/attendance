package com.smart.attendance.service;

import com.smart.attendance.dto.ChangePasswordDTO;
import com.smart.attendance.dto.LoginRequestDTO;
import com.smart.attendance.dto.LoginResponseDTO;

import lombok.Data;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO request);

    String registerStudent(String email,
                           String password,
                           String name,
                           String rollNo,
                           String classEmail);

  


   String changePassword(String email, ChangePasswordDTO dto);

String registerFaculty(String email,
                       String password,
                       String name,
                       String department,
                       boolean isTutor,
                       String secretCode);

String registerHod(String email,
                   String password,
                   String name,
                   String secretCode);
}