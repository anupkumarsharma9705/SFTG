package com.securetransfer.SFTG.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class FileResponseDTO {

    private String originalFilename;
    private String mimeType;
    private Long fileSize;
    private LocalDateTime uploadDate;
    private LocalDateTime expiryAt;

}
