package com.airline.controller;

import com.airline.service.OtpService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendOtp(@RequestParam String phone, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (phone == null || phone.trim().length() < 7) {
                response.put("success", false);
                response.put("message", "Please enter a valid mobile contact number.");
                return ResponseEntity.badRequest().body(response);
            }

            String otpCode = otpService.sendOtp(phone, session);

            response.put("success", true);
            response.put("message", "OTP sent successfully to " + phone + ". Please enter the 6-digit code to verify.");
            response.put("demoOtp", otpCode); // Included for convenient testing
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to send OTP: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestParam String phone,
                                                         @RequestParam String otp,
                                                         HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean isVerified = otpService.verifyOtp(phone, otp, session);

            if (isVerified) {
                response.put("success", true);
                response.put("message", "Mobile number verified successfully!");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Invalid or expired OTP. Verification failed.");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error verifying OTP: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
