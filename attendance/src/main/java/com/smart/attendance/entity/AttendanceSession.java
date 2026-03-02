package com.smart.attendance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "attendance_sessions")
public class AttendanceSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String classEmail;

    private String subject;

    private String period;

    private double latitude;

    private double longitude;

    private double radius;

    @Column(unique = true)
    private String qrToken;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private boolean active;
    private boolean expired;

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private Faculty createdBy;

    @OneToMany(mappedBy = "session")
    private List<Attendance> attendances;
}