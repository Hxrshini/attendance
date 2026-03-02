package com.smart.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.attendance.entity.Faculty;
import com.smart.attendance.entity.ProfileEditRequest;

import java.util.List;

public interface ProfileEditRequestRepository extends JpaRepository<ProfileEditRequest, Long> {

    List<ProfileEditRequest> findByStatus(String status);

    List<ProfileEditRequest> findByStudentId(Long studentId);
    List<ProfileEditRequest> findByStudent_Tutor_IdAndStatus(
            Long tutorId, String status);
     List<ProfileEditRequest> findByStudent_TutorAndStatus(Faculty tutor, String status);
    
}