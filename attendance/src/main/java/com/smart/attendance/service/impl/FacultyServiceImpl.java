package com.smart.attendance.service.impl;

import com.smart.attendance.entity.*;
import com.smart.attendance.repository.*;
import com.smart.attendance.service.FacultyService;
import lombok.RequiredArgsConstructor;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FacultyServiceImpl implements FacultyService {

    private final AttendanceSessionRepository sessionRepository;
    private final AttendanceRepository attendanceRepository;
    private final FacultyRepository facultyRepository;
    private final AttendanceSessionRepository attendanceSessionRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    @Override
    public AttendanceSession startSession(String classEmail,
                                          String subject,
                                          String period,
                                          double latitude,
                                          double longitude,
                                          double radius,
                                          int durationMinutes,
                                          Long facultyId) {

        // 1️⃣ Validate duration
        if (durationMinutes <= 0 || durationMinutes > 15) {
            throw new RuntimeException("Session duration must be between 1 and 15 minutes");
        }

        // 2️⃣ Prevent multiple active sessions per class
        boolean alreadyActive = sessionRepository
                .findByClassEmailAndActiveTrue(classEmail)
                .size() > 0;

        if (alreadyActive) {
            throw new RuntimeException("An active session already exists for this class");
        }

        // 3️⃣ Get faculty
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("Faculty not found"));

        // 4️⃣ Create session
        AttendanceSession session = AttendanceSession.builder()
                .classEmail(classEmail)
                .subject(subject)
                .period(period)
                .latitude(latitude)
                .longitude(longitude)
                .radius(radius)
                .qrToken(UUID.randomUUID().toString())
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusMinutes(durationMinutes))
                .active(true)
                .expired(false)
                .createdBy(faculty)
                .build();

        return sessionRepository.save(session);
    }

    @Override
    public String closeSession(Long sessionId) {

        AttendanceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        session.setActive(false);
        session.setExpired(true);
        session.setEndTime(LocalDateTime.now());

        sessionRepository.save(session);

        return "Session closed successfully";
    }

    @Override
    public String manualEditAttendance(Long attendanceId, boolean present) {

        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new RuntimeException("Attendance not found"));

        attendance.setPresent(present);
        attendanceRepository.save(attendance);

        return "Attendance updated successfully";
    }

    @Override
    public List<AttendanceSession> getActiveSessionsByFaculty(Long facultyId) {

        return sessionRepository.findByCreatedById(facultyId)
                .stream()
                .filter(AttendanceSession::isActive)
                .toList();
    }
    @Override
public byte[] generateSessionExcel(Long sessionId,
                                   Authentication authentication) throws Exception {

    // 1️⃣ Get logged-in faculty
    String email = authentication.getName();

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Faculty faculty = facultyRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("Faculty not found"));

    // 2️⃣ Get session
    AttendanceSession session = attendanceSessionRepository
            .findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));

    // 3️⃣ Security check (VERY IMPORTANT)
    if (!session.getCreatedBy().getId().equals(faculty.getId())) {
        throw new RuntimeException("Not authorized to access this session");
    }

    String classEmail = session.getClassEmail();
    String className = classEmail.substring(0, classEmail.indexOf("@"));

    List<Student> students = studentRepository.findByClassEmail(classEmail);

    long totalStudents = students.size();

    long presentCount =
            attendanceRepository.countBySession_IdAndPresentTrue(sessionId);

    long absentCount = totalStudents - presentCount;

    List<Attendance> sessionAttendances =
            attendanceRepository.findBySession_Id(sessionId);

    List<String> presentRollNos = sessionAttendances.stream()
            .filter(Attendance::isPresent)
            .map(a -> a.getStudent().getRollNo())
            .toList();

    List<String> absentRollNos = students.stream()
            .map(Student::getRollNo)
            .filter(roll -> !presentRollNos.contains(roll))
            .toList();

    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Session Report");

    int rowNum = 0;

    Row header = sheet.createRow(rowNum++);
    header.createCell(0).setCellValue("Class Name");
    header.createCell(1).setCellValue(className);

    Row row1 = sheet.createRow(rowNum++);
    row1.createCell(0).setCellValue("Total Students");
    row1.createCell(1).setCellValue(totalStudents);

    Row row2 = sheet.createRow(rowNum++);
    row2.createCell(0).setCellValue("Present");
    row2.createCell(1).setCellValue(presentCount);

    Row row3 = sheet.createRow(rowNum++);
    row3.createCell(0).setCellValue("Absent");
    row3.createCell(1).setCellValue(absentCount);

    rowNum++;

    Row absentHeader = sheet.createRow(rowNum++);
    absentHeader.createCell(0).setCellValue("Absent Roll Numbers");

    for (String roll : absentRollNos) {
        Row r = sheet.createRow(rowNum++);
        r.createCell(0).setCellValue(roll);
    }

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    workbook.write(out);
    workbook.close();

    return out.toByteArray();
}
@Override
public byte[] downloadActiveSessionReport(Authentication authentication) throws Exception {

    // 1️⃣ Get logged-in faculty
    String email = authentication.getName();

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Faculty faculty = facultyRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("Faculty not found"));

    // 2️⃣ Get active session
    AttendanceSession session = attendanceSessionRepository
            .findByCreatedByAndActiveTrue(faculty)
            .orElseThrow(() -> new RuntimeException("No active session found"));

    // 3️⃣ Reuse existing method
    return generateSessionExcel(session.getId(), authentication);
}

   
}