package com.securetransfer.SFTG.controller;

import com.securetransfer.SFTG.dto.ShareLinkRequest;
import com.securetransfer.SFTG.model.FileEntity;
import com.securetransfer.SFTG.model.SharedLink;
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

@RestController
@RequestMapping("/api/share")
@RequiredArgsConstructor
public class FileShareController {

    private final FileShareService fileShareService;
    private final FileService fileService;

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


//    @PostMapping("/generate/{storedFilename}")
//    public ResponseEntity<String> generateShareLink(
//            @PathVariable String storedFilename,
//            Authentication authentication) {
//
//        String username = authentication.getName();
//        String link = fileShareService.generateShareLink(storedFilename, username);
//
//        return ResponseEntity.ok(link);
//    }

    @GetMapping("/download/{token}")
    public ResponseEntity<Resource> downloadSharedFile(
            @PathVariable String token) {

        FileEntity file = fileShareService.validateAndGetFile(token);

        Resource resource = fileService.loadSharedFileAsResource(
                file.getStoredFilename()
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getOriginalFilename() + "\"")
                .body(resource);
    }

//    public ResponseEntity<Resource> downloadSharedFile(
//            @PathVariable String token,
//            Authentication authentication) {
//
//        // 🔐 ensures user is logged in
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return ResponseEntity
//                    .status(401)
//                    .header("Error", "Authentication is required to download shared files")
//                    .build();
//        }
//
//        FileEntity file = fileShareService.validateAndGetFile(token);
//
//        Resource resource = fileService.loadFileAsResource(
//                file.getStoredFilename(),
//                file.getUploadedByUsername().getUsername()
//        );
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION,
//                        "attachment; filename=\"" + file.getOriginalFilename() + "\"")
//                .body(resource);
//    }

//    @GetMapping("/download/{token}")
//    public ResponseEntity<Resource> downloadSharedFile(@PathVariable String token) {
//
//        SharedLink link = fileShareService.validateLink(token);
//
//        Resource resource = fileService.loadFileAsResource(
//                link.getStoredFilename(),
//                link.getCreatedByUsername()
//        );
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION,
//                        "attachment; filename=\"" + link.getOriginalFilename() + "\"")
//                .body(resource);
//    }
}
