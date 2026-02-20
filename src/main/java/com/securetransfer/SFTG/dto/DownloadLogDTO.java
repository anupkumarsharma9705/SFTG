package com.securetransfer.SFTG.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class DownloadLogDTO {

    private String downloaderEmail;
    private LocalDateTime downloadedAt;

}

