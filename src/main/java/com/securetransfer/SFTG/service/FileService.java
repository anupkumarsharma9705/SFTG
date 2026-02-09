// src/main/java/com/example/sftg/service/FileService.java
package com.securetransfer.SFTG.service;

import com.securetransfer.SFTG.exception.FileStorageException;
import com.securetransfer.SFTG.exception.ResourceNotFoundException;
import com.securetransfer.SFTG.model.FileEntity;
import com.securetransfer.SFTG.model.User;
import com.securetransfer.SFTG.repository.FileRepository;
import com.securetransfer.SFTG.repository.UserRepository;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private Path fileStorageLocation;

    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private ClamAVService clamAVService;

    @Autowired
    private UserRepository userRepository;

    private Tika tika; // Apache Tika instance

    @PostConstruct
    public void init() {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
        this.tika = new Tika(); // Initialize Tika
    }

    public FileEntity storeFile(MultipartFile file, String username) {
        // Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileStorageException("File size exceeds the maximum limit of " + MAX_FILE_SIZE / (1024 * 1024) + "MB.");
        }

        // Normalize file name (prevent path traversal)
        String originalFilename = org.springframework.util.StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (originalFilename.contains("..")) {
            throw new FileStorageException("Sorry! Filename contains invalid path sequence " + originalFilename);
        }

        // Generate a unique filename to store on disk (UUID based)
        String storedFilename = UUID.randomUUID().toString() + "_" + originalFilename;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(storedFilename);

            // Copy file content
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 🔐 SECURITY: Virus scan using ClamAV
            try {
                clamAVService.scanFile(targetLocation);
            } catch (Exception ex) {
                Files.deleteIfExists(targetLocation); // cleanup infected file
                throw ex;
            }

            // IMPORTANT SECURITY MEASURE: Remove executable permissions (best effort, OS dependent)
            // This is crucial to prevent uploaded files from being executed on the server.
            // IMPORTANT SECURITY MEASURE: Remove execute permissions (POSIX systems only)
            try {
                Files.setPosixFilePermissions(
                        targetLocation,
                        PosixFilePermissions.fromString("rw-r--r--")
                );
            } catch (UnsupportedOperationException e) {
                // Non-POSIX file system (Windows, etc.) — safe to ignore
            }

//            try {
//                // Example for Unix-like systems: remove 'x' (execute) permission for owner, group, and others
//                // This requires the 'unix:permissions' attribute which might not be available on all file systems.
//                // A more robust solution involves storing user-uploaded content on a dedicated, non-executable mount point.
//                String permissions = (String) Files.getAttribute(targetLocation, "unix:permissions", LinkOption.NOFOLLOW_LINKS);
//                String newPermissions = permissions.replaceAll("x", "-"); // Remove all 'x' characters
//                Files.setAttribute(targetLocation, "unix:permissions", newPermissions, LinkOption.NOFOLLOW_LINKS);
//            } catch (UnsupportedOperationException e) {
//                // Not a Unix-like system or attribute not supported, log this info
//                System.out.println("Unix permissions not supported or attribute 'unix:permissions' not available on this OS for " + targetLocation + ": " + e.getMessage());
//            } catch (IOException e) {
//                throw new FileStorageException("Could not modify permissions of file " + storedFilename, e);
//            }


            // Detect MIME type using Apache Tika (content-based detection for security)
            String mimeType;
            try (InputStream stream = Files.newInputStream(targetLocation)) {
                mimeType = tika.detect(stream, originalFilename);
            } catch (IOException e) {
                System.err.println("Could not detect MIME type with Tika for " + storedFilename + ". Falling back to generic type. Error: " + e.getMessage());
                mimeType = "application/octet-stream"; // Fallback to generic binary
            }
            if (mimeType.equals("application/x-msdownload")
                    || mimeType.equals("application/x-sh")
                    || mimeType.equals("application/x-bat")) {
                Files.deleteIfExists(targetLocation);
                throw new FileStorageException("Executable files are not allowed for security reasons");
            }

            // Get user from DB
            User uploader = userRepository.findByEmail(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Uploader user not found: " + username));

            // Save file metadata to database
            FileEntity fileEntity = new FileEntity();
            fileEntity.setOriginalFilename(originalFilename);
            fileEntity.setStoredFilename(storedFilename);
            fileEntity.setMimeType(mimeType);
            fileEntity.setFileSize(file.getSize());
            fileEntity.setUploadDate(LocalDateTime.now());
            fileEntity.setExpiryAt(LocalDateTime.now().plusHours(1)); // ⏱ auto-expire after 1 hour
            fileEntity.setUploadedByUsername(uploader);
            fileEntity.setFilePath(targetLocation.toString()); // ✅ IMPORTANT

            return fileRepository.save(fileEntity);

        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + storedFilename + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String storedFilename, String requestingUsername) {
        FileEntity fileEntity = fileRepository.findByStoredFilename(storedFilename)
                .orElseThrow(() -> new ResourceNotFoundException("File not found " + storedFilename));

        // IMPORTANT SECURITY MEASURE: Enforce file ownership authorization
        if (!fileEntity.getUploadedByUsername().getEmail().equals(requestingUsername)) {
            throw new FileStorageException("Access denied: You are not authorized to download this file.");
        }

        try {
            Path filePath = this.fileStorageLocation.resolve(fileEntity.getStoredFilename()).normalize();

            // Prevent path traversal
            if (!filePath.startsWith(this.fileStorageLocation)) {
                throw new FileStorageException("Attempted to access file outside of designated upload directory: " + storedFilename);
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found " + storedFilename);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File not found " + storedFilename, ex);
        }
    }

    public Resource loadSharedFileAsResource(String storedFilename) {
        FileEntity fileEntity = fileRepository.findByStoredFilename(storedFilename)
                .orElseThrow(() ->
                        new ResourceNotFoundException("File not found: " + storedFilename)
                );
        try {
            Path filePath = this.fileStorageLocation
                    .resolve(fileEntity.getStoredFilename())
                    .normalize();
            // ✅ PATH TRAVERSAL PROTECTION
            if (!filePath.startsWith(this.fileStorageLocation)) {
                throw new FileStorageException("Invalid file path access attempt");
            }
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new ResourceNotFoundException("File not found: " + storedFilename);
            }
            return resource;
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File not found: " + storedFilename, ex);
        }
    }


    public Optional<FileEntity> getFileMetadataByStoredFilename(String storedFilename) {
        return fileRepository.findByStoredFilename(storedFilename);
    }
}
