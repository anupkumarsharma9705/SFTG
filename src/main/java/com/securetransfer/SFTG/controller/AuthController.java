// src/main/java/com/securetransfer/SFTG/controller/AuthController.java
package com.securetransfer.SFTG.controller;

import com.securetransfer.SFTG.dto.*;
import com.securetransfer.SFTG.service.AuthService;
import com.securetransfer.SFTG.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.ok("OTP sent to email");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpRequest request) {
        userService.verifyOtp(request);
        return ResponseEntity.ok("Email verified successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}


//package com.securetransfer.SFTG.controller;
//
//import com.securetransfer.SFTG.dto.*;
//import com.securetransfer.SFTG.model.User;
//import com.securetransfer.SFTG.service.AuthService;
//import com.securetransfer.SFTG.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/auth")
//public class AuthController {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private AuthService authService;
//
//    @PostMapping("/register")
//    public ResponseEntity<UserResponse> registerUser(@RequestBody RegisterRequest registerRequest) {
//        User registeredUser = userService.registerUser(registerRequest);
//        UserResponse userResponse = new UserResponse(registeredUser.getId(), registeredUser.getUsername(), registeredUser.getRoles());
//        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
//    }
//
//    @PostMapping("/verify-otp")
//    public ResponseEntity<String> verifyOtp(@RequestBody OtpRequest request) {
//        userService.verifyOtp(request);
//        return ResponseEntity.ok("OTP verified successfully");
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
//        String token = authService.authenticateUser(authRequest);
//        return ResponseEntity.ok(new AuthResponse(token));
//    }
//}