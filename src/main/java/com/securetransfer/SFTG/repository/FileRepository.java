// src/main/java/com/example/sftg/repository/FileRepository.java
package com.securetransfer.SFTG.repository;

import com.securetransfer.SFTG.model.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    Optional<FileEntity> findByStoredFilename(String storedFilename);
}

//import com.securetransfer.SFTG.model.FileEntity;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//@Repository
//public interface FileRepository extends JpaRepository<FileEntity, Long> {
//
//    Optional<FileEntity> findByFilename(String filename);
//}


//package com.securetransfer.SFTG.repository;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import com.securetransfer.SFTG.model.FileEntity;
//
//public interface FileRepository extends JpaRepository<FileEntity, Long> {
//    FileEntity findByFilename(String filename);
//}
