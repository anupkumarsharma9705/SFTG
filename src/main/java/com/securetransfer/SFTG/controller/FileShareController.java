package com.securetransfer.SFTG.controller;

import com.securetransfer.SFTG.dto.ShareLinkRequest;
import com.securetransfer.SFTG.model.DownloadLog;
import com.securetransfer.SFTG.model.FileEntity;
import com.securetransfer.SFTG.model.SharedLink;
import com.securetransfer.SFTG.repository.DownloadLogRepository;
import com.securetransfer.SFTG.service.FileService;
import com.securetransfer.SFTG.service.FileShareService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/share")
@RequiredArgsConstructor
public class FileShareController {

    private final FileShareService fileShareService;
    private final FileService fileService;
    private final DownloadLogRepository downloadLogRepository;

    @PostMapping("/generate")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> generateShareLink(
            @Valid @RequestBody ShareLinkRequest request,
            Authentication authentication) {

        String username = authentication.getName();
        String link = fileShareService.generateShareLink(
                request.getStoredFilename(),
                username
        );

        return ResponseEntity.ok(link);
    }

    @GetMapping("/download/{token}")
    public ResponseEntity<Resource> downloadSharedFile(
            @PathVariable String token,
            Authentication authentication) {

        SharedLink sharedLink = fileShareService.validateLink(token);

        FileEntity file = fileService.getFileByStoredFilename(
                sharedLink.getStoredFilename()
        );

        // Save log
        DownloadLog log = DownloadLog.builder()
                .downloaderEmail(authentication != null ? authentication.getName() : "ANONYMOUS")
                .downloadedAt(LocalDateTime.now())
                .sharedLink(sharedLink)
                .build();

        downloadLogRepository.save(log);

        Resource resource = fileService.loadSharedFileAsResource(
                file.getStoredFilename()
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getOriginalFilename() + "\"")
                .body(resource);
    }

//    @GetMapping("/download/{token}")
//    public ResponseEntity<Resource> downloadSharedFile(
//            @PathVariable String token) {
//
//        FileEntity file = fileShareService.validateAndGetFile(token);
//
//        Resource resource = fileService.loadSharedFileAsResource(
//                file.getStoredFilename()
//        );
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION,
//                        "attachment; filename=\"" + file.getOriginalFilename() + "\"")
//                .body(resource);
//    }
}
