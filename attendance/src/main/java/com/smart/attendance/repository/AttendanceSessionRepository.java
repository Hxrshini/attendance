package com.smart.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.attendance.entity.AttendanceSession;
import com.smart.attendance.entity.Faculty;

import java.util.Optional;
import java.util.List;

public interface AttendanceSessionRepository extends JpaRepository<AttendanceSession, Long> {

    Optional<AttendanceSession> findByQrToken(String qrToken);

    List<AttendanceSession> findByClassEmailAndActiveTrue(String classEmail);

    List<AttendanceSession> findByCreatedById(Long facultyId);

    Optional<AttendanceSession> findByQrTokenAndActiveTrue(String qrToken);
 boolean existsByCreatedByAndActiveTrue(Faculty createdBy); 
 Optional<AttendanceSession> findByCreatedByAndActiveTrue(Faculty faculty);
}