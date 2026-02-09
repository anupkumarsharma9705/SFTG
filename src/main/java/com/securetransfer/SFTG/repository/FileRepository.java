// src/main/java/com/example/sftg/repository/FileRepository.java
package com.securetransfer.SFTG.repository;

import com.securetransfer.SFTG.model.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    List<FileEntity> findByExpiryAtBefore(LocalDateTime time);
    Optional<FileEntity> findByStoredFilename(String storedFilename);
}