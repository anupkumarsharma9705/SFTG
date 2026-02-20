package com.securetransfer.SFTG.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "download_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DownloadLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String downloaderEmail; // can be null for anonymous

    private LocalDateTime downloadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_link_id", nullable = false)
    private SharedLink sharedLink;
}
