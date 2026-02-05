// src/main/java/com/securetransfer/SFTG/dto/AuthRequest.java
package com.securetransfer.SFTG.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}


//package com.securetransfer.SFTG.dto;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class AuthRequest {
//    private String username;
//    private String password;
//}