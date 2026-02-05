// src/main/java/com/securetransfer/SFTG/service/AuthService.java
package com.securetransfer.SFTG.service;

import com.securetransfer.SFTG.dto.AuthRequest;
import com.securetransfer.SFTG.model.User;
import com.securetransfer.SFTG.repository.UserRepository;
import com.securetransfer.SFTG.security.JwtTokenUtil;
import com.securetransfer.SFTG.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public String login(AuthRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEmailVerified()) {
            throw new RuntimeException("Email not verified");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(request.getEmail());

        return jwtTokenUtil.generateToken(userDetails);
    }
}


//package com.securetransfer.SFTG.service;
//
//import com.securetransfer.SFTG.dto.AuthRequest;
//import com.securetransfer.SFTG.model.User;
//import com.securetransfer.SFTG.security.JwtTokenUtil;
//import com.securetransfer.SFTG.security.UserDetailsServiceImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//
//@Service
//public class AuthService {
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @Autowired
//    private UserDetailsServiceImpl userDetailsService;
//
//    @Autowired
//    private JwtTokenUtil jwtTokenUtil;
//
//    public String authenticateUser(AuthRequest authRequest) {
//
//        User user = userRepository.findByUsername(authRequest.getUsername())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        if (!user.isVerified()) {
//            throw new RuntimeException("Please verify OTP before login");
//        }
//
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        authRequest.getUsername(),
//                        authRequest.getPassword()
//                )
//        );
//
//        UserDetails userDetails =
//                userDetailsService.loadUserByUsername(authRequest.getUsername());
//
//        return jwtTokenUtil.generateToken(userDetails);
//    }
//
//
////    public String authenticateUser(AuthRequest authRequest) {
////        try {
////            authenticationManager.authenticate(
////                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
////            );
////        } catch (BadCredentialsException e) {
////            throw new BadCredentialsException("Incorrect username or password", e);
////        }
////
////        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
////        return jwtTokenUtil.generateToken(userDetails);
////    }
//}