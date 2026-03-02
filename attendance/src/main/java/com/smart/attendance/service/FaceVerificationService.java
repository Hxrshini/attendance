package com.smart.attendance.service;

public interface FaceVerificationService {

    boolean verifyFace(String registeredPhotoPath,
                       String selfiePath);
}