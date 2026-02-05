// src/main/java/com/example/sftg/dto/FileResponse.java
package com.securetransfer.SFTG.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileResponse {
    private Long id;
    private String originalFilename;
    private String storedFilename;
    private String mimeType;
    private Long fileSize;
    private LocalDateTime uploadDate;
    private String uploadedByUsername; // To show who uploaded the file
}