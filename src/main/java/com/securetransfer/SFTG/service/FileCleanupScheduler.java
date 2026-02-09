package com.securetransfer.SFTG.service;

import com.securetransfer.SFTG.model.FileEntity;
import com.securetransfer.SFTG.repository.FileRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class FileCleanupScheduler {

    private final FileRepository fileRepository;

    public FileCleanupScheduler(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Scheduled(fixedRate = 5 * 60 * 1000) // runs every 5 minutes
    public void deleteExpiredFiles() {
        List<FileEntity> expiredFiles =
                fileRepository.findByExpiryAtBefore(LocalDateTime.now());

        for (FileEntity file : expiredFiles) {
            try {
                Files.deleteIfExists(Paths.get(file.getFilePath()));
                fileRepository.delete(file);
            } catch (Exception e) {
                System.err.println("Failed to delete expired file: " + file.getFilePath());
            }
        }
    }
}
