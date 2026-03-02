package com.smart.attendance.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.smart.attendance.dto.AttendanceReportDTO;
import com.smart.attendance.dto.ManualEditDTO;
import com.smart.attendance.dto.StartSessionDTO;
import com.smart.attendance.entity.Attendance;
import com.smart.attendance.entity.AttendanceSession;
import com.smart.attendance.entity.Faculty;
import com.smart.attendance.entity.Role;
import com.smart.attendance.entity.User;
import com.smart.attendance.repository.AttendanceRepository;
import com.smart.attendance.repository.AttendanceSessionRepository;
import com.smart.attendance.repository.FacultyRepository;
import com.smart.attendance.repository.UserRepository;
import com.smart.attendance.service.SessionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final AttendanceSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final FacultyRepository facultyRepository;
    private final AttendanceRepository attendanceRepository;

  @Override
public String startSession(StartSessionDTO dto,
                           Authentication authentication) {

    // 1️⃣ Get logged-in email
    String email = authentication.getName();

    // 2️⃣ Find user
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Faculty not found"));

    // 3️⃣ Check role
    if (!user.getRole().equals(Role.FACULTY)) {
        throw new RuntimeException("Not authorized");
    }

    // 4️⃣ Get faculty entity
    Faculty faculty = facultyRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("Faculty details missing"));

    // 5️⃣ Prevent multiple sessions
    if (sessionRepository.existsByCreatedByAndActiveTrue(faculty)) {
        throw new RuntimeException("Active session already exists");
    }

    LocalDateTime start = LocalDateTime.now();
    LocalDateTime end = start.plusMinutes(dto.getDurationMinutes());

    AttendanceSession session = AttendanceSession.builder()
            .createdBy(faculty)
            .classEmail(dto.getClassEmail())
            .subject(dto.getSubject())
            .period(dto.getPeriod())
            .latitude(dto.getLatitude())
            .longitude(dto.getLongitude())
            .radius(dto.getRadius())
            .startTime(start)
            .endTime(end)
            .active(true)
            .expired(false)
            .qrToken(UUID.randomUUID().toString())
            .build();

    sessionRepository.save(session);

    return "Session started successfully";
}

    @Override
    public String closeSession(Long sessionId, Authentication authentication) {

        AttendanceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        session.setActive(false);
        session.setExpired(true);

        sessionRepository.save(session);

        return "Session closed successfully";
    }

    @Override
public List<AttendanceReportDTO> getSessionReport(Long sessionId,
                                                  Authentication authentication) {

    AttendanceSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));

    List<Attendance> attendanceList =
            attendanceRepository.findBySession(session);

    return attendanceList.stream()
            .map(att -> AttendanceReportDTO.builder()
                    .rollNo(att.getStudent().getRollNo())
                    .studentName(att.getStudent().getUser().getName())
                    .subject(session.getSubject())
                    .present(att.isPresent())
                    .markedAt(att.getMarkedAt())
                    .build())
            .toList();
}
@Override
public String manualEdit(ManualEditDTO dto, Authentication authentication) {

    String email = authentication.getName();

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Faculty faculty = facultyRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("Faculty not found"));

    AttendanceSession session = sessionRepository.findById(dto.getSessionId())
            .orElseThrow(() -> new RuntimeException("Session not found"));

    if (!session.getCreatedBy().getUser().getEmail().equals(email)) {
    throw new RuntimeException("Not authorized to edit this session");
}

    Attendance attendance = attendanceRepository
            .findBySessionIdAndStudentId(dto.getSessionId(), dto.getStudentId())
            .orElseThrow(() -> new RuntimeException("Attendance not found"));

    attendance.setPresent(dto.isPresent());
    attendanceRepository.save(attendance);

    return "Attendance updated successfully";
}
    
}