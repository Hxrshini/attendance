package com.smart.attendance.controller;

import com.smart.attendance.dto.*;
import com.smart.attendance.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
   private final AuthenticationManager authenticationManager;
    private final AuthService authService;

    

    @PostMapping("/register-student")
    public String registerStudent(@RequestBody RegisterStudentDTO dto) {
        return authService.registerStudent(
                dto.getEmail(),
                dto.getPassword(),
                dto.getName(),
                dto.getRollNo(),
                dto.getClassEmail()
        );
    }

    @PostMapping("/register-faculty")
public String registerFaculty(@RequestBody RegisterFacultyDTO dto) {

    return authService.registerFaculty(
            dto.getEmail(),
            dto.getPassword(),     // ✅ ADD THIS
            dto.getName(),
            dto.getDepartment(),
            dto.isTutor(),
            dto.getSecretCode()
    );
}
@PostMapping("/register-hod")
public String registerHod(@RequestBody RegisterHodDTO dto) {

    return authService.registerHod(
            dto.getEmail(),
            dto.getPassword(),     // ✅ ADD THIS
            dto.getName(),
            dto.getSecretCode()
    );
}
@PostMapping("/login")
public ResponseEntity<LoginResponseDTO> login(
        @RequestBody LoginRequestDTO request,
        HttpServletRequest httpRequest) {

    Authentication authentication =
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

    SecurityContextHolder.getContext().setAuthentication(authentication);

    httpRequest.getSession(true).setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            SecurityContextHolder.getContext()
    );

    return ResponseEntity.ok(
            LoginResponseDTO.builder()
                    .role(authentication.getAuthorities().toString())
                    .message("Login successful")
                    .build()
    );
}
}