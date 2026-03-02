package com.smart.attendance.service.impl;

import com.smart.attendance.dto.*;
import com.smart.attendance.entity.*;
import com.smart.attendance.repository.*;
import com.smart.attendance.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.faculty.secret}")
    private String appFacultySecret;

    @Value("${app.hod.secret}")
    private String appHodSecret;

    // ✅ SIMPLE LOGIN (NO JWT)
    @Override
public LoginResponseDTO login(LoginRequestDTO request) {

    User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Invalid credentials"));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new RuntimeException("Invalid credentials");
    }

    return LoginResponseDTO.builder()
            .role(user.getRole().name())
            .message("Login successful")
            .build();
}

@Override
public String registerStudent(String email,
                              String password,
                              String name,
                              String rollNo,
                              String classEmail) {

    if (userRepository.existsByEmail(email)) {
        throw new RuntimeException("Email already exists");
    }

    // 1️⃣ Create User
    User user = User.builder()
            .email(email)
            .password(passwordEncoder.encode(password))
            .name(name)
            .role(Role.STUDENT)
            .active(true)
            .firstLogin(true)
            .build();

    user = userRepository.saveAndFlush(user);

    // 2️⃣ FORCE tutor to exist
    Faculty tutor = facultyRepository
            .findByAssignedClassEmail(classEmail)
            .orElseThrow(() -> 
                new RuntimeException("No tutor assigned for this class"));

    // 3️⃣ Create Student
    Student student = Student.builder()
            .user(user)
            .rollNo(rollNo)
            .classEmail(classEmail)
            .tutor(tutor)  // ✅ will NEVER be null now
            .build();

    studentRepository.save(student);

    return "Student registered successfully";
}
    // ✅ REGISTER FACULTY
    @Override
    public String registerFaculty(String email,
                                  String password,
                                  String name,
                                  String department,
                                  boolean isTutor,
                                  String secretCode) {

        if (!secretCode.equals(appFacultySecret)) {
            throw new RuntimeException("Invalid Faculty Secret Code");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .role(Role.FACULTY)
                .active(true)
                .firstLogin(false)
                .build();

        userRepository.save(user);

        Faculty faculty = Faculty.builder()
                .user(user)
                .department(department)
                .isTutor(isTutor)
                .build();

        facultyRepository.save(faculty);

        return "Faculty registered successfully";
    }

    // ✅ REGISTER HOD
    @Override
    public String registerHod(String email,
                              String password,
                              String name,
                              String secretCode) {

        if (!secretCode.equals(appHodSecret)) {
            throw new RuntimeException("Invalid HOD Secret Code");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .role(Role.HOD)
                .active(true)
                .firstLogin(false)
                .build();

        userRepository.save(user);

        return "HOD registered successfully";
    }

    // ✅ CHANGE PASSWORD
    @Override
    public String changePassword(String email, ChangePasswordDTO dto) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password incorrect");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        return "Password changed successfully";
    }
}