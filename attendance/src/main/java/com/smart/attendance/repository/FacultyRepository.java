package com.smart.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.attendance.entity.Faculty;
import com.smart.attendance.entity.ProfileEditRequest;
import com.smart.attendance.entity.User;

import java.util.Optional;
import java.util.List;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    Optional<Faculty> findByAssignedClassEmail(String classEmail);

    List<Faculty> findByIsTutorTrue();

    Optional<Faculty> findByUserEmail(String email);

 //Optional<ProfileEditRequest> findByUser(User user);
    Optional<Faculty> findByUser(User user);

}