package com.smart.attendance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "profile_edit_requests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileEditRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    private String requestedField;
    private String newValue;

    private String status;  // PENDING / APPROVED / REJECTED

    private LocalDateTime requestedAt;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private Faculty approvedBy;
}
