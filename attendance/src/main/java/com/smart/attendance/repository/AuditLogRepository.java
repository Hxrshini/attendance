package com.smart.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.attendance.entity.AuditLog;
import com.smart.attendance.entity.Faculty;
import com.smart.attendance.entity.ProfileEditRequest;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByPerformedBy(String performedBy);
    List<AuditLog> findByStudent_ClassEmail(String classEmail);
    List<AuditLog> findByStudent_Tutor(Faculty tutor);
}