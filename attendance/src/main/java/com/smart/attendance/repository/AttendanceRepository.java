package com.smart.attendance.repository;

import com.smart.attendance.entity.Attendance;
import com.smart.attendance.entity.AttendanceSession;
import com.smart.attendance.entity.Faculty;
import com.smart.attendance.entity.Student;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    long countByPresentTrue();
    long countByPresentFalse();

    @Query("""
        SELECT a.student.rollNo
        FROM Attendance a
        WHERE a.present = false
        AND DATE(a.markedAt) = CURRENT_DATE
    """)
    List<String> findTodayAbsenteesRollNo();

    @Query("""
        SELECT a FROM Attendance a
        WHERE DATE(a.markedAt) = CURRENT_DATE
    """)
    List<Attendance> findDailyReport();

    @Query("""
        SELECT a FROM Attendance a
        WHERE a.markedAt >= :startDate
    """)
    List<Attendance> findReportAfter(@Param("startDate") LocalDateTime startDate);

    @Query("""
        SELECT a.student.id,
               SUM(CASE WHEN a.present = true THEN 1 ELSE 0 END),
               COUNT(a)
        FROM Attendance a
        GROUP BY a.student.id
    """)
    List<Object[]> getAttendanceSummary();

    // FIXED
    Optional<Attendance> findByStudentIdAndSessionId(Long studentId, Long sessionId);

    @Query("""
        SELECT a
        FROM Attendance a
        WHERE a.student.tutor.department = :department
    """)
    List<Attendance> findByDepartment(@Param("department") String department);

    @Query("""
        SELECT a
        FROM Attendance a
        WHERE a.session.classEmail = :classEmail
    """)
    List<Attendance> findByClassEmail(@Param("classEmail") String classEmail);

    @Query("""
        SELECT a
        FROM Attendance a
        WHERE a.student.tutor.department = :department
        AND a.session.classEmail = :classEmail
    """)
    List<Attendance> findByDepartmentAndClass(
            @Param("department") String department,
            @Param("classEmail") String classEmail
    );
 
    List<Attendance> findByStudentId(Long studentId);
    @Query("""
    SELECT a FROM Attendance a
    WHERE a.student.classEmail = :classEmail
""")
List<Attendance> findFilteredAttendance(@Param("classEmail") String classEmail);
    boolean existsByStudentIdAndSessionId(Long studentId, Long sessionId);
    Attendance findTopByStudentIdOrderByMarkedAtDesc(Long studentId);
    long countByStudentId(Long studentId);

long countByStudentIdAndPresentTrue(Long studentId);

boolean existsByStudentIdAndMarkedAtBetween(
        Long studentId,
        java.time.LocalDateTime start,
        java.time.LocalDateTime end
);
List<Attendance> findByStudent_ClassEmail(String classEmail);

 List<Attendance> findBySession(AttendanceSession session);
Optional<Attendance> findBySessionIdAndStudentId(Long sessionId, Long studentId);
long countByStudent_Id(Long studentId);

long countByStudent_IdAndPresentTrue(Long studentId);
List<Attendance> findByStudent_Id(Long studentId);
long countBySession_IdAndPresentTrue(Long sessionId);

List<Attendance> findBySession_Id(Long sessionId);
@Query("""
    SELECT a FROM Attendance a
    WHERE DATE(a.markedAt) = CURRENT_DATE
""")
List<Attendance> findTodayAttendance();
}