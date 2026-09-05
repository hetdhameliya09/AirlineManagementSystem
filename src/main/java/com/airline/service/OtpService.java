package com.airline.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private static final int OTP_LENGTH = 6;
    private static final long OTP_VALID_DURATION_MINUTES = 5;
    private static final SecureRandom random = new SecureRandom();

    @Value("${otp.sms.api.key:}")
    private String smsApiKey;

    @Value("${otp.sms.enabled:true}")
    private boolean smsEnabled;

    // Class to represent stored OTP details
    public static class OtpData {
        private final String code;
        private final String phone;
        private final Instant expiryTime;

        public OtpData(String code, String phone, Instant expiryTime) {
            this.code = code;
            this.phone = phone;
            this.expiryTime = expiryTime;
        }

        public String getCode() {
            return code;
        }

        public String getPhone() {
            return phone;
        }

        public boolean isExpired() {
            return Instant.now().isAfter(expiryTime);
        }
    }

    // In-memory fallback map if session is not available
    private final Map<String, OtpData> otpStorage = new ConcurrentHashMap<>();

    /**
     * Generate a 6-digit random numeric OTP
     */
    public String generateOtpCode() {
        int number = random.nextInt(900000) + 100000;
        return String.valueOf(number);
    }

    /**
     * Generate OTP, store it in session and memory, and send SMS to user phone
     */
    public String sendOtp(String phone, HttpSession session) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number is required");
        }

        String cleanPhone = phone.replaceAll("[^0-9]", "");
        String otpCode = generateOtpCode();
        Instant expiryTime = Instant.now().plus(Duration.ofMinutes(OTP_VALID_DURATION_MINUTES));

        OtpData otpData = new OtpData(otpCode, cleanPhone, expiryTime);

        // Store in HTTP Session
        if (session != null) {
            session.setAttribute("REGISTER_OTP_" + cleanPhone, otpData);
            session.setAttribute("CURRENT_OTP_PHONE", cleanPhone);
        }
        // Also store in thread-safe map
        otpStorage.put(cleanPhone, otpData);

        System.out.println("=================================================");
        System.out.println("[REAL OTP SYSTEM] Generated OTP for Phone: " + phone + " (" + cleanPhone + ")");
        System.out.println("[REAL OTP SYSTEM] OTP CODE: " + otpCode + " (Valid for 5 minutes)");
        System.out.println("=================================================");

        // Dispatch Real SMS
        dispatchSms(cleanPhone, otpCode);

        return otpCode;
    }

    /**
     * Dispatch SMS using Real SMS Gateway API (Fast2SMS / Generic REST API)
     */
    private void dispatchSms(String phone, String otpCode) {
        if (!smsEnabled) {
            System.out.println("[REAL OTP SYSTEM] SMS sending disabled in configuration.");
            return;
        }

        if (smsApiKey == null || smsApiKey.trim().isEmpty() || "YOUR_FAST2SMS_API_KEY".equals(smsApiKey)) {
            System.out.println("[REAL OTP SYSTEM] Note: 'otp.sms.api.key' is not set in application.properties.");
            System.out.println("[REAL OTP SYSTEM] Real SMS delivery requires an API key. Using console fallback code: " + otpCode);
            return;
        }

        try {
            // Fast2SMS API integration endpoint (Indian Numbers)
            String apiUrl = "https://www.fast2sms.com/dev/bulkV2?authorization=" 
                    + URLEncoder.encode(smsApiKey, StandardCharsets.UTF_8)
                    + "&route=otp&variables_values=" + URLEncoder.encode(otpCode, StandardCharsets.UTF_8)
                    + "&numbers=" + URLEncoder.encode(phone, StandardCharsets.UTF_8);

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        System.out.println("[REAL OTP SYSTEM] SMS Gateway Response Status: " + response.statusCode());
                        System.out.println("[REAL OTP SYSTEM] Response Body: " + response.body());
                    })
                    .exceptionally(ex -> {
                        System.err.println("[REAL OTP SYSTEM] Failed to send SMS via Gateway API: " + ex.getMessage());
                        return null;
                    });

        } catch (Exception e) {
            System.err.println("[REAL OTP SYSTEM] Error initiating SMS dispatch: " + e.getMessage());
        }
    }

    /**
     * Verify provided OTP code against stored OTP for the phone number
     */
    public boolean verifyOtp(String phone, String inputOtp, HttpSession session) {
        if (phone == null || inputOtp == null) {
            return false;
        }

        String cleanPhone = phone.replaceAll("[^0-9]", "");
        String cleanInput = inputOtp.trim();

        OtpData otpData = null;

        // Try getting from session first
        if (session != null) {
            otpData = (OtpData) session.getAttribute("REGISTER_OTP_" + cleanPhone);
        }

        // Fallback to memory storage
        if (otpData == null) {
            otpData = otpStorage.get(cleanPhone);
        }

        if (otpData == null) {
            System.out.println("[REAL OTP SYSTEM] Verification failed: No OTP found for phone " + cleanPhone);
            return false;
        }

        if (otpData.isExpired()) {
            System.out.println("[REAL OTP SYSTEM] Verification failed: OTP expired for phone " + cleanPhone);
            if (session != null) session.removeAttribute("REGISTER_OTP_" + cleanPhone);
            otpStorage.remove(cleanPhone);
            return false;
        }

        boolean matches = otpData.getCode().equals(cleanInput);
        if (matches) {
            System.out.println("[REAL OTP SYSTEM] Verification SUCCESS for phone " + cleanPhone);
            if (session != null) {
                session.setAttribute("OTP_VERIFIED_PHONE", cleanPhone);
                session.removeAttribute("REGISTER_OTP_" + cleanPhone);
            }
            otpStorage.remove(cleanPhone);
            return true;
        } else {
            System.out.println("[REAL OTP SYSTEM] Verification FAILED: Input OTP '" + cleanInput + "' does not match expected OTP for " + cleanPhone);
            return false;
        }
    }

    /**
     * Check if phone was already verified in session
     */
    public boolean isPhoneVerified(String phone, HttpSession session) {
        if (phone == null || session == null) return false;
        String cleanPhone = phone.replaceAll("[^0-9]", "");
        String verifiedPhone = (String) session.getAttribute("OTP_VERIFIED_PHONE");
        return cleanPhone.equals(verifiedPhone);
    }
}
