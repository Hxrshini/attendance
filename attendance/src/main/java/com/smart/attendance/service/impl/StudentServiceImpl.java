package com.smart.attendance.service.impl;

import org.springframework.security.core.Authentication;
import com.smart.attendance.dto.*;
import com.smart.attendance.entity.*;
import com.smart.attendance.repository.*;
import com.smart.attendance.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final UserRepository userRepository;
    private final AttendanceSessionRepository sessionRepository;
    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final ProfileEditRequestRepository profileEditRequestRepository;

    // =====================================================
    // 🔐 COMMON METHOD (VERY IMPORTANT)
    // =====================================================

    private Student getLoggedInStudent(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getRole().equals(Role.STUDENT)) {
            throw new RuntimeException("Not authorized");
        }

        return studentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    // =====================================================
    // ✅ MARK ATTENDANCE
    // =====================================================

    @Override
    public String markAttendance(MarkAttendanceDTO dto,
                                 Authentication authentication) {

        Student student = getLoggedInStudent(authentication);

        AttendanceSession session = sessionRepository
                .findByQrToken(dto.getQrToken())
                .orElseThrow(() -> new RuntimeException("Invalid QR Code"));

        if (!session.isActive()) {
            throw new RuntimeException("Session is closed");
        }

        if (LocalDateTime.now().isAfter(session.getEndTime())) {
            session.setActive(false);
            session.setExpired(true);
            sessionRepository.save(session);
            throw new RuntimeException("QR expired");
        }

        boolean alreadyMarked =
                attendanceRepository.existsByStudentIdAndSessionId(
                        student.getId(),
                        session.getId());

        if (alreadyMarked) {
            throw new RuntimeException("Attendance already marked");
        }

        Attendance attendance = Attendance.builder()
                .student(student)
                .session(session)
                .present(true)
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .selfiePath(dto.getSelfiePath())
                .markedAt(LocalDateTime.now())
                .build();

        attendanceRepository.save(attendance);

        return "Attendance marked successfully";
    }

    // =====================================================
    // ✅ ATTENDANCE HISTORY
    // =====================================================

    @Override
    public List<AttendanceReportDTO> getAttendanceHistory(Authentication authentication) {

        Student student = getLoggedInStudent(authentication);

        return attendanceRepository
                .findByStudent_Id(student.getId())
                .stream()
                .map(att -> AttendanceReportDTO.builder()
                        .rollNo(student.getRollNo())
                        .studentName(student.getUser().getName())
                        .subject(att.getSession().getSubject())
                        .present(att.isPresent())
                        .markedAt(att.getMarkedAt())
                        .build())
                .toList();
    }

    // =====================================================
    // ✅ ATTENDANCE PERCENTAGE
    // =====================================================

    @Override
    public Double getAttendancePercentage(Authentication authentication) {

        Student student = getLoggedInStudent(authentication);

        long totalClasses =
                attendanceRepository.countByStudent_Id(student.getId());

        if (totalClasses == 0) {
            return 0.0;
        }

        long totalPresent =
                attendanceRepository.countByStudent_IdAndPresentTrue(student.getId());

        return (totalPresent * 100.0) / totalClasses;
    }

    // =====================================================
    // ✅ PROFILE
    // =====================================================

    @Override
    public StudentProfileDTO getProfile(Authentication authentication) {

        Student student = getLoggedInStudent(authentication);

        User user = student.getUser();
        Faculty tutor = student.getTutor();

        return StudentProfileDTO.builder()
                .studentId(student.getId())
                .name(user.getName())
                .email(user.getEmail())
                .rollNo(student.getRollNo())
                .classEmail(student.getClassEmail())
                .department(tutor != null ? tutor.getDepartment() : null)
                .tutorName(tutor != null ? tutor.getUser().getName() : null)
                .profilePhotoPath(student.getProfilePhotoPath())
                .active(user.isActive())
                .build();
    }

    // =====================================================
    // ✅ DASHBOARD
    // =====================================================

    @Override
    public StudentDashboardDTO getDashboard(Authentication authentication) {

        Student student = getLoggedInStudent(authentication);

        long totalClasses =
                attendanceRepository.countByStudentId(student.getId());

        long totalPresent =
                attendanceRepository.countByStudentIdAndPresentTrue(student.getId());

        long totalAbsent = totalClasses - totalPresent;

        double percentage = totalClasses == 0
                ? 0.0
                : (totalPresent * 100.0) / totalClasses;

        boolean markedToday =
                attendanceRepository.existsByStudentIdAndMarkedAtBetween(
                        student.getId(),
                        java.time.LocalDate.now().atStartOfDay(),
                        java.time.LocalDate.now().atTime(23, 59, 59)
                );

        return StudentDashboardDTO.builder()
                .name(student.getUser().getName())
                .rollNo(student.getRollNo())
                .totalClasses(totalClasses)
                .totalPresent(totalPresent)
                .totalAbsent(totalAbsent)
                .percentage(percentage)
                .markedToday(markedToday)
                .build();
    }

    // =====================================================
    // ✅ FIRST TIME PROFILE SETUP
    // =====================================================

    @Override
    public String updateProfileFirstTime(Authentication authentication,
                                         FirstTimeProfileDTO dto) {

        Student student = getLoggedInStudent(authentication);
        User user = student.getUser();

        if (!user.isFirstLogin()) {
            throw new RuntimeException("Profile already set.");
        }

        student.setRollNo(dto.getRollNo());
        student.setProfilePhotoPath(dto.getProfilePhotoPath());
        student.setMacAddress(dto.getMacAddress());

        user.setName(dto.getName());
        user.setFirstLogin(false);

        studentRepository.save(student);
        userRepository.save(user);

        return "Profile set successfully";
    }

    // =====================================================
    // ✅ REQUEST PROFILE EDIT
    // =====================================================

    @Override
    public String requestProfileEdit(Authentication authentication,
                                     ProfileEditRequestDTO dto) {

        Student student = getLoggedInStudent(authentication);
        User user = student.getUser();

        if (user.isFirstLogin()) {
            throw new RuntimeException("Complete first-time profile setup first.");
        }

        ProfileEditRequest request = ProfileEditRequest.builder()
                .student(student)
                .requestedField(dto.getField())
                .newValue(dto.getNewValue())
                .status("PENDING")
                .requestedAt(LocalDateTime.now())
                .build();

        profileEditRequestRepository.save(request);

        return "Profile edit request sent to tutor";
    }
}