// src/main/java/com/example/sftg/dto/UserResponse.java
package com.securetransfer.SFTG.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import com.securetransfer.SFTG.model.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private Set<Role> roles;
}