package com.smart.attendance.service.impl;

import com.smart.attendance.entity.AttendanceSession;
import com.smart.attendance.repository.AttendanceSessionRepository;
import com.smart.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceSessionRepository sessionRepository;

    @Override
    public AttendanceSession validateAndGetActiveSession(String qrToken) {

        AttendanceSession session = sessionRepository
                .findByQrToken(qrToken)
                .orElseThrow(() -> new RuntimeException("Invalid QR Code"));

        // 1️⃣ Check if session already closed
        if (!session.isActive()) {
            throw new RuntimeException("Session is already closed");
        }

        // 2️⃣ Check expiry time
        if (LocalDateTime.now().isAfter(session.getEndTime())) {

            session.setActive(false);
            session.setExpired(true);
            sessionRepository.save(session);

            throw new RuntimeException("QR Code expired");
        }

        return session;
    }

    @Override
    public void closeSession(Long sessionId) {

        AttendanceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        session.setActive(false);
        session.setExpired(true);

        sessionRepository.save(session);
    }
}