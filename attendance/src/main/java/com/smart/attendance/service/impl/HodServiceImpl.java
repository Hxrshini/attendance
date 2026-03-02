package com.smart.attendance.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.smart.attendance.dto.AttendanceReportDTO;
import com.smart.attendance.dto.DepartmentReportDTO;
import com.smart.attendance.entity.Attendance;
import com.smart.attendance.entity.Faculty;
import com.smart.attendance.entity.Student;
import com.smart.attendance.repository.AttendanceRepository;
import com.smart.attendance.repository.FacultyRepository;
import com.smart.attendance.repository.StudentRepository;
import com.smart.attendance.service.HodService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HodServiceImpl implements HodService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    // Summary report
    @Override
    public DepartmentReportDTO getDepartmentReport() {

        long totalPresent = attendanceRepository.countByPresentTrue();
        long totalAbsent = attendanceRepository.countByPresentFalse();

        List<String> absentees =
                attendanceRepository.findTodayAbsenteesRollNo();

        return DepartmentReportDTO.builder()
                .totalPresent(totalPresent)
                .totalAbsent(totalAbsent)
                .absenteesRollNumbers(absentees)
                .build();
    }

    // Daily
 @Override
public List<AttendanceReportDTO> getDailyReport() {
    List<Attendance> list = attendanceRepository.findDailyReport();
    return list.stream().map(this::mapToDTO).toList();
}
@Override
public List<AttendanceReportDTO> getWeeklyReport() {

    LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);

    return attendanceRepository.findReportAfter(oneWeekAgo)
            .stream()
            .map(this::mapToDTO)
            .toList();
}

   @Override
public List<AttendanceReportDTO> getMonthlyReport() {

    LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);

    return attendanceRepository.findReportAfter(oneMonthAgo)
            .stream()
            .map(this::mapToDTO)
            .toList();
}

 
    private AttendanceReportDTO mapToDTO(Attendance a) {

    return AttendanceReportDTO.builder()
            .rollNo(a.getStudent().getRollNo())
            .studentName(a.getStudent().getUser().getName())
            .subject(a.getSession().getSubject())
            .present(a.isPresent())
            .markedAt(a.getMarkedAt())
            .build();
}

@Override
public List<AttendanceReportDTO> getAttendanceHistory(Long studentId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getAttendanceHistory'");
}

@Override
public String assignTutor(Long facultyId, String classEmail) {

    Faculty faculty = facultyRepository.findById(facultyId)
            .orElseThrow(() -> new RuntimeException("Faculty not found"));

    // Direct assignment (no validation)
    faculty.setTutor(true);
    faculty.setAssignedClassEmail(classEmail);

    facultyRepository.save(faculty);
    

    return "Class assigned successfully to faculty " + facultyId;
}
@Override
public byte[] generateDailyReportExcel(Authentication authentication) throws Exception {

    List<Attendance> todayAttendance = attendanceRepository.findTodayAttendance();

    if (todayAttendance.isEmpty()) {
        throw new RuntimeException("No attendance data for today");
    }

    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Daily Report");

    int rowNum = 0;

    Row header = sheet.createRow(rowNum++);
    header.createCell(0).setCellValue("Class");
    header.createCell(1).setCellValue("Total Students");
    header.createCell(2).setCellValue("Present");
    header.createCell(3).setCellValue("Absent");

    Map<String, List<Attendance>> grouped =
            todayAttendance.stream()
                    .collect(Collectors.groupingBy(a -> a.getSession().getClassEmail()));

    for (String classEmail : grouped.keySet()) {

        String className = classEmail.substring(0, classEmail.indexOf("@"));

        List<Student> students =
                studentRepository.findByClassEmail(classEmail);

        long totalStudents = students.size();

        long presentCount =
                grouped.get(classEmail).stream()
                        .filter(Attendance::isPresent)
                        .count();

        long absentCount = totalStudents - presentCount;

        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(className);
        row.createCell(1).setCellValue(totalStudents);
        row.createCell(2).setCellValue(presentCount);
        row.createCell(3).setCellValue(absentCount);

        rowNum++;

        Row absentHeader = sheet.createRow(rowNum++);
        absentHeader.createCell(0).setCellValue("Absent Roll Numbers");

        List<String> presentRollNos =
                grouped.get(classEmail).stream()
                        .filter(Attendance::isPresent)
                        .map(a -> a.getStudent().getRollNo())
                        .toList();

        for (Student s : students) {
            if (!presentRollNos.contains(s.getRollNo())) {
                Row r = sheet.createRow(rowNum++);
                r.createCell(0).setCellValue(s.getRollNo());
            }
        }

        rowNum++;
    }

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    workbook.write(out);
    workbook.close();

    return out.toByteArray();
}
private byte[] generateGroupedExcel(List<Attendance> attendanceList,
                                    String sheetName) throws Exception {

    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet(sheetName);

    int rowNum = 0;

    Row header = sheet.createRow(rowNum++);
    header.createCell(0).setCellValue("Class");
    header.createCell(1).setCellValue("Total Students");
    header.createCell(2).setCellValue("Present");
    header.createCell(3).setCellValue("Absent");

    Map<String, List<Attendance>> grouped =
            attendanceList.stream()
                    .collect(Collectors.groupingBy(a -> a.getSession().getClassEmail()));

    for (String classEmail : grouped.keySet()) {

        String className = classEmail.substring(0, classEmail.indexOf("@"));

        List<Student> students =
                studentRepository.findByClassEmail(classEmail);

        long totalStudents = students.size();

        long presentCount =
                grouped.get(classEmail).stream()
                        .filter(Attendance::isPresent)
                        .count();

        long absentCount = totalStudents - presentCount;

        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(className);
        row.createCell(1).setCellValue(totalStudents);
        row.createCell(2).setCellValue(presentCount);
        row.createCell(3).setCellValue(absentCount);

        rowNum++;

        Row absentHeader = sheet.createRow(rowNum++);
        absentHeader.createCell(0).setCellValue("Absent Roll Numbers");

        List<String> presentRollNos =
                grouped.get(classEmail).stream()
                        .filter(Attendance::isPresent)
                        .map(a -> a.getStudent().getRollNo())
                        .toList();

        for (Student s : students) {
            if (!presentRollNos.contains(s.getRollNo())) {
                Row r = sheet.createRow(rowNum++);
                r.createCell(0).setCellValue(s.getRollNo());
            }
        }

        rowNum++;
    }

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    workbook.write(out);
    workbook.close();

    return out.toByteArray();
}
@Override
public byte[] generateWeeklyReportExcel(Authentication authentication) throws Exception {

    LocalDateTime startOfWeek = LocalDate.now()
            .with(DayOfWeek.MONDAY)
            .atStartOfDay();

    List<Attendance> weeklyAttendance =
            attendanceRepository.findReportAfter(startOfWeek);

    if (weeklyAttendance.isEmpty()) {
        throw new RuntimeException("No attendance data for this week");
    }

    return generateGroupedExcel(weeklyAttendance, "Weekly Report");
}
@Override
public byte[] generateMonthlyReportExcel(Authentication authentication) throws Exception {

    LocalDateTime startOfMonth =
            LocalDate.now().withDayOfMonth(1).atStartOfDay();

    List<Attendance> monthlyAttendance =
            attendanceRepository.findReportAfter(startOfMonth);

    if (monthlyAttendance.isEmpty()) {
        throw new RuntimeException("No attendance data for this month");
    }

    return generateGroupedExcel(monthlyAttendance, "Monthly Report");
}


}