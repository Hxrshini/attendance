package com.smart.attendance.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "students")
public class Student {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @Column(unique = true, nullable = false)
    private String rollNo;

    @Column(nullable = false)
    private String classEmail;

    @ManyToOne
    @JoinColumn(name = "tutor_id")
    private Faculty tutor;

    @Column(unique = true)
    private String macAddress;

    private String profilePhotoPath;
    
     
}