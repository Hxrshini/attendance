package com.smart.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.attendance.entity.Student;
import com.smart.attendance.entity.User;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByClassEmail(String classEmail);

    List<Student> findByTutorId(Long tutorId);

    Optional<Student> findByUser(User user);

    boolean existsByMacAddress(String macAddress);
}