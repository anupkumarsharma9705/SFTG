// src/main/java/com/example/sftg/dto/AuthResponse.java
package com.securetransfer.SFTG.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String jwtToken;
}
