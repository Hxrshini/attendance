package com.smart.attendance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "faculty")
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private String department;

    private boolean isTutor;

    private String assignedClassEmail;
@OneToMany(mappedBy = "tutor")
@Builder.Default
private List<Student> students = new ArrayList<>();
}