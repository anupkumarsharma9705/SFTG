// src/main/java/com/example/sftg/controller/FileController.java
package com.securetransfer.SFTG.controller;

import com.securetransfer.SFTG.dto.FileResponse;
import com.securetransfer.SFTG.exception.ResourceNotFoundException;
import com.securetransfer.SFTG.model.FileEntity;
import com.securetransfer.SFTG.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<FileResponse> uploadFile(@RequestParam("file") MultipartFile file, Principal principal) {
        FileEntity fileEntity = fileService.storeFile(file, principal.getName()); // Get username from authenticated principal

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/files/download/")
                .path(fileEntity.getStoredFilename())
                .toUriString();

        FileResponse fileResponse = new FileResponse(
                fileEntity.getId(),
                fileEntity.getOriginalFilename(),
                fileEntity.getStoredFilename(),
                fileEntity.getMimeType(),
                fileEntity.getFileSize(),
                fileEntity.getUploadDate(),
                fileEntity.getUploadedByUsername().getEmail()
        );
        return ResponseEntity.ok(fileResponse);
    }

    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String storedFilename,
            Authentication authentication) {

        Resource resource = fileService.loadFileAsResource(
                storedFilename,
                authentication.getName()
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

//    public ResponseEntity<Resource> downloadFile(@PathVariable String filename, HttpServletRequest request, Principal principal) {
//        // Load file as Resource and perform ownership check in service
//        FileEntity fileEntity = fileService.getFileMetadataByStoredFilename(filename)
//                .orElseThrow(() -> new ResourceNotFoundException("File metadata not found for " + filename));
//
//        // Delegate to service for loading and ownership check
//        Resource resource = fileService.loadFileAsResource(fileEntity.getStoredFilename(), principal.getName());
//
//        // Try to determine file's content type from resource, fallback to stored MIME type
//        String contentType = null;
//        try {
//            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
//        } catch (IOException ex) {
//            // Log this, but fall back to database stored MIME type
//            System.out.println("Could not determine file type from servlet context. Falling back to stored MIME type. Error: " + ex.getMessage());
//        }
//
//        // Fallback to the database-stored MIME type if not determined from servlet context
//        if (contentType == null || MediaType.APPLICATION_OCTET_STREAM_VALUE.equals(contentType)) { // If generic or null
//            contentType = fileEntity.getMimeType();
//        }
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(contentType))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileEntity.getOriginalFilename() + "\"")
//                .body(resource);
//    }
}

//import com.securetransfer.SFTG.model.FileEntity;
//import com.securetransfer.SFTG.service.FileService;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.*;
//import java.nio.file.*;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/files")
//public class FileController {
//
//    private final FileService fileService;
//
//    public FileController(FileService fileService) {
//        this.fileService = fileService;
//    }
//
//    @PostMapping("/upload")
//    public ResponseEntity<?> uploadFile(
//            @RequestParam("file") MultipartFile file,
//            @AuthenticationPrincipal UserDetails userDetails) {
//
//        try {
//            FileEntity saved = fileService.uploadFile(file, userDetails.getUsername());
//            return ResponseEntity.ok("File uploaded successfully: " + saved.getFilename());
//        } catch (IOException e) {
//            return ResponseEntity.internalServerError().body("File upload failed: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/download/{filename}")
//    public void downloadFile(@PathVariable String filename, HttpServletResponse response) throws IOException {
//        Optional<FileEntity> fileEntity = fileService.getFileByName(filename);
//
//        if (fileEntity.isEmpty()) {
//            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            response.getWriter().write("File not found");
//            return;
//        }
//
//        Path filePath = Paths.get(fileEntity.get().getFilePath());
//        if (Files.exists(filePath)) {
//            response.setContentType("application/octet-stream");
//            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
//            Files.copy(filePath, response.getOutputStream());
//            response.getOutputStream().flush();
//        } else {
//            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            response.getWriter().write("File not found on disk");
//        }
//    }
//}
