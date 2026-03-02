package com.smart.attendance.service.impl;

import com.smart.attendance.dto.AttendanceReportDTO;
import com.smart.attendance.dto.ProfileEditRequestResponseDTO;
import com.smart.attendance.entity.*;
import com.smart.attendance.repository.*;
import com.smart.attendance.service.TutorService;
import lombok.RequiredArgsConstructor;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TutorServiceImpl implements TutorService {

    private final FacultyRepository facultyRepository;
    private final AttendanceRepository attendanceRepository;
    private final ProfileEditRequestRepository profileEditRequestRepository;
    private final StudentRepository studentRepository;
    private final AuditLogRepository auditLogRepository;
    private final DeviceChangeLogRepository deviceChangeLogRepository;
    private final UserRepository userRepository; // ✅ FIXED (was missing)
    private Faculty getLoggedInTutor(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return facultyRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Tutor not found"));
    }


  @Override
public List<AttendanceReportDTO> getClassReport(Authentication authentication) {

    Faculty tutor = getLoggedInTutor(authentication);

    return attendanceRepository
            .findByStudent_ClassEmail(tutor.getAssignedClassEmail())
            .stream()
            .map(a -> AttendanceReportDTO.builder()
                    .rollNo(a.getStudent().getRollNo())
                    .studentName(a.getStudent().getUser().getName())
                    .subject(a.getSession().getSubject())
                    .present(a.isPresent())
                    .markedAt(a.getMarkedAt())
                    .build())
            .toList();
}

    // ✅ Suspicious logs (with tutorId)
     @Override
    public List<AuditLog> getSuspiciousLogs(Authentication authentication) {

        Faculty tutor = getLoggedInTutor(authentication);

        return auditLogRepository
                .findByStudent_Tutor(tutor);
    }

    // ✅ Reset student MAC
    @Override
    public String resetStudentMac(Long tutorId,
                                  Long studentId,
                                  String newMac) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (!student.getTutor().getId().equals(tutorId)) {
            throw new RuntimeException("Not authorized");
        }

        String oldMac = student.getMacAddress();

        student.setMacAddress(newMac);
        studentRepository.save(student);

        deviceChangeLogRepository.save(
                DeviceChangeLog.builder()
                        .oldMac(oldMac)
                        .newMac(newMac)
                        .student(student)
                        .changedBy(student.getTutor())
                        .changedAt(LocalDateTime.now())
                        .build()
        );

        return "MAC updated successfully";
    }


    // ===================================================
    // Device History
    // ===================================================

    @Override
    public List<DeviceChangeLog> getDeviceChangeHistory(Authentication authentication) {

        Faculty tutor = getLoggedInTutor(authentication);

        return deviceChangeLogRepository
                .findByStudent_Tutor(tutor);
    }

 // ===================================================
    // Reject Profile
    // ===================================================

    @Override
    public String rejectProfileEdit(Long requestId,
                                    Authentication authentication) {

        Faculty tutor = getLoggedInTutor(authentication);

        ProfileEditRequest request =
                profileEditRequestRepository.findById(requestId)
                        .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getStudent().getTutor().getId().equals(tutor.getId())) {
            throw new RuntimeException("Not authorized");
        }

        request.setStatus("REJECTED");
        request.setApprovedBy(tutor);
        profileEditRequestRepository.save(request);
        profileEditRequestRepository.delete(request);
        return "Profile edit rejected";
    }
@Override
public String approveProfileEdit(Long requestId,
                                 Authentication authentication) {

    Faculty tutor = getLoggedInTutor(authentication);

    ProfileEditRequest request =
            profileEditRequestRepository.findById(requestId)
                    .orElseThrow(() -> new RuntimeException("Request not found"));

    Student student = request.getStudent();

    if (student.getTutor() == null ||
        !student.getTutor().getId().equals(tutor.getId())) {
        throw new RuntimeException("Not authorized");
    }

    String field = request.getRequestedField().trim().toLowerCase();

    switch (field) {

        case "name":
            student.getUser().setName(request.getNewValue());
            userRepository.save(student.getUser());
            break;

        case "rollno":
        case "roll_no":
            student.setRollNo(request.getNewValue());
            break;

        case "classemail":
        case "class_email":
            student.setClassEmail(request.getNewValue());
            break;

        case "profilephotopath":
        case "profile_photo_path":
            student.setProfilePhotoPath(request.getNewValue());
            break;

        default:
            throw new RuntimeException("Invalid requested field: " + field);
    }

    studentRepository.save(student);

    // 🔥 AUTO DELETE REQUEST
    profileEditRequestRepository.delete(request);

    return "Profile edit approved and request removed successfully";
}


 @Override
public List<ProfileEditRequestResponseDTO> getPendingRequests(Authentication authentication) {

    Faculty tutor = getLoggedInTutor(authentication);

    return profileEditRequestRepository
            .findByStudent_TutorAndStatus(tutor, "PENDING")
            .stream()
            .map(req -> ProfileEditRequestResponseDTO.builder()
                    .requestId(req.getId())
                    .studentName(req.getStudent().getUser().getName())
                    .rollNo(req.getStudent().getRollNo())
                    .requestedField(req.getRequestedField())
                    .newValue(req.getNewValue())
                    .status(req.getStatus())
                    .build())
            .toList();
}
@Override
public byte[] downloadClassReportExcel(Authentication authentication) throws Exception {

    Faculty tutor = getLoggedInTutor(authentication);

    String classEmail = tutor.getAssignedClassEmail();
    String className = classEmail.substring(0, classEmail.indexOf("@"));

    List<Student> students = studentRepository.findByClassEmail(classEmail);

    long totalStudents = students.size();

    List<Attendance> attendances =
            attendanceRepository.findByStudent_ClassEmail(classEmail);

    long presentCount = attendances.stream()
            .filter(Attendance::isPresent)
            .count();

    long absentCount = totalStudents - presentCount;

    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Class Report");

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

    rowNum += 2;

    Row tableHeader = sheet.createRow(rowNum++);
    tableHeader.createCell(0).setCellValue("Roll No");
    tableHeader.createCell(1).setCellValue("Student Name");
    tableHeader.createCell(2).setCellValue("Subject");
    tableHeader.createCell(3).setCellValue("Present");
    tableHeader.createCell(4).setCellValue("Marked At");

    for (Attendance a : attendances) {
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(a.getStudent().getRollNo());
        row.createCell(1).setCellValue(a.getStudent().getUser().getName());
        row.createCell(2).setCellValue(a.getSession().getSubject());
        row.createCell(3).setCellValue(a.isPresent() ? "Yes" : "No");
        row.createCell(4).setCellValue(a.getMarkedAt().toString());
    }

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    workbook.write(out);
    workbook.close();

    return out.toByteArray();
}
}
