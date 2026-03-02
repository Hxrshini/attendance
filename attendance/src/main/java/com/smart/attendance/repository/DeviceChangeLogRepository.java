package com.smart.attendance.repository;

import com.smart.attendance.entity.DeviceChangeLog;
import com.smart.attendance.entity.Faculty;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceChangeLogRepository
        extends JpaRepository<DeviceChangeLog, Long> {

    List<DeviceChangeLog> findByStudent_ClassEmail(String classEmail);
List<DeviceChangeLog> findByStudent_Tutor(Faculty tutor);
}