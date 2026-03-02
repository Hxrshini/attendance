package com.smart.attendance.service.impl;

import com.smart.attendance.service.FaceVerificationService;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class FaceVerificationServiceImpl implements FaceVerificationService {

    @Override
    public boolean verifyFace(String registeredPhotoPath,
                              String selfiePath) {

        // 🚀 DEMO MODE LOGIC

        if (registeredPhotoPath == null || selfiePath == null) {
            return false;
        }

        File registered = new File(registeredPhotoPath);
        File selfie = new File(selfiePath);

        // If both files exist → assume match (Demo AI Mode)
        return registered.exists() && selfie.exists();
    }
}