// src/main/java/com/example/sftg/model/FileEntity.java
package com.securetransfer.SFTG.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "files")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false, unique = true)
    private String storedFilename; // UUID based filename on disk

    @Column(nullable = false)
    private String mimeType;

    @Column(nullable = false)
    private Long fileSize; // in bytes

    @Column(nullable = false)
    private LocalDateTime uploadDate;

    @Column(nullable = false)
    private LocalDateTime expiryAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User uploadedByUsername;
}

//import jakarta.persistence.*;
//import jakarta.persistence.UniqueConstraint;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.time.LocalDateTime;
//
//@Getter
//@Setter
//@Entity
//@Table(name = "files", uniqueConstraints = {
//        @UniqueConstraint(columnNames = "filename")
//})
//public class FileEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, length = 255)
//    private String filename;
//
//    @Column(nullable = false)
//    private String filePath;
//
//    @Column(nullable = false, length = 100)
//    private String username;
//
//    @Column(nullable = false)
//    private LocalDateTime uploadTime;
//
//    @Column(length = 255)
//    private String originalFilename;
//
//    @Column(length = 100)
//    private String detectedMimeType;
//
//    public FileEntity() {}
//
//    public FileEntity(String filename, String filePath, String username, LocalDateTime uploadTime) {
//        this.filename = filename;
//        this.filePath = filePath;
//        this.username = username;
//        this.uploadTime = uploadTime;
//    }
//
//    @Override
//    public String toString() {
//        return "FileEntity{" +
//                "id=" + id +
//                ", filename='" + filename + '\'' +
//                ", username='" + username + '\'' +
//                ", uploadTime=" + uploadTime +
//                ", detectedMimeType='" + detectedMimeType + '\'' +
//                '}';
//    }
//}
