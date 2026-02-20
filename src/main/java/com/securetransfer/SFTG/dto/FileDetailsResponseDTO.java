package com.securetransfer.SFTG.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class FileDetailsResponseDTO {

    private Long id;
    private String originalFilename;
    private Long fileSize;
    private LocalDateTime uploadDate;
    private LocalDateTime expiryAt;

    private boolean active;
    private int downloadLimit;
    private int downloadCount;

    private List<DownloadLogDTO> downloadLogs;
}
