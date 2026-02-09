// src/main/java/com/securetransfer/SFTG/service/UserService.java
package com.securetransfer.SFTG.service;

import com.securetransfer.SFTG.dto.OtpRequest;
import com.securetransfer.SFTG.dto.RegisterRequest;
import com.securetransfer.SFTG.model.Role;
import com.securetransfer.SFTG.model.User;
import com.securetransfer.SFTG.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;
    public User register(RegisterRequest request) {

        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        // CASE 1: Email exists & already verified → block registration
        if (user != null && user.isEmailVerified()) {
            throw new RuntimeException("Account already exists. Please login.");
        }

        // Generate OTP
        String otp = String.valueOf(100000 + new SecureRandom().nextInt(900000));
        long expiryTime = System.currentTimeMillis() + 5 * 60 * 1000;

        // CASE 2: Email exists but NOT verified → resend OTP
        if (user != null && !user.isEmailVerified()) {

            user.setOtp(otp);
            user.setOtpExpiryTime(expiryTime);
            userRepository.save(user);

            emailService.sendOtp(user.getEmail(), otp);
            return user;
        }

        // CASE 3: New email → create new user
        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setEmailVerified(false);
        newUser.setRoles(Set.of(Role.ROLE_USER));
        newUser.setOtp(otp);
        newUser.setOtpExpiryTime(expiryTime);

        userRepository.save(newUser);
        emailService.sendOtp(newUser.getEmail(), otp);

        return newUser;
    }

//    public User register(RegisterRequest request) {
//
//        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
//            throw new RuntimeException("Email already registered");
//        }
//
//        User user = new User();
//        user.setEmail(request.getEmail());
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
//        user.setRoles(Set.of(Role.ROLE_USER));
//
//        String otp = String.valueOf(100000 + new Random().nextInt(900000));
//        user.setOtp(otp);
//        user.setOtpExpiryTime(System.currentTimeMillis() + 5 * 60 * 1000);
//
//        userRepository.save(user);
//        emailService.sendOtp(user.getEmail(), otp);
//
//        return user;
//    }

    public void verifyOtp(OtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isEmailVerified()) {
            throw new RuntimeException("Already verified");
        }

        if (!request.getOtp().equals(user.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        if (user.getOtpExpiryTime() < System.currentTimeMillis()) {
            throw new RuntimeException("OTP expired");
        }

        user.setEmailVerified(true);
        user.setOtp(null);
        user.setOtpExpiryTime(null);
        userRepository.save(user);
    }
}
