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
@Table(name = "device_change_logs")
public class DeviceChangeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String oldMac;
    private String newMac;

    private LocalDateTime changedAt;

    @ManyToOne
    private Student student;

    @ManyToOne
    private Faculty changedBy;
}