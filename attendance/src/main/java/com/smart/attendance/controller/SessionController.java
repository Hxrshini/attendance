package com.smart.attendance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.smart.attendance.service.EmailService;

@RestController
@RequestMapping("/api/session")
public class SessionController {

    @GetMapping("/active/{classEmail}")
    public String getActiveSession(@PathVariable String classEmail) {
        return "Active session for class";
    }

    @PutMapping("/close/{sessionId}")
    public String closeSession(@PathVariable Long sessionId) {
        return "Close session";
    }
    @Autowired
private EmailService emailService;

@GetMapping("/test-mail")
public String testMail() {

    emailService.sendSimpleMail(
            "harshini270905@gmail.com",
            "Test Mail",
            "Email service working!"
    );

    return "Mail Sent";
}

}