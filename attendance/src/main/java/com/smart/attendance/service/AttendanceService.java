package com.smart.attendance.service;

import com.smart.attendance.entity.AttendanceSession;

public interface AttendanceService {

    AttendanceSession validateAndGetActiveSession(String qrToken);

    void closeSession(Long sessionId);
}