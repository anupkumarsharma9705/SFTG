// src/main/java/com/securetransfer/SFTG/dto/OtpRequest.java
package com.securetransfer.SFTG.dto;

import lombok.Data;

@Data
public class OtpRequest {
    private String email;
    private String otp;
}
