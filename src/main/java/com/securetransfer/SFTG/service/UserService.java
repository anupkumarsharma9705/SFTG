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

import java.util.Random;
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

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(Role.ROLE_USER));

        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        user.setOtp(otp);
        user.setOtpExpiryTime(System.currentTimeMillis() + 5 * 60 * 1000);

        userRepository.save(user);
        emailService.sendOtp(user.getEmail(), otp);

        return user;
    }

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
