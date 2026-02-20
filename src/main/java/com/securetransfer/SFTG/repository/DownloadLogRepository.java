package com.securetransfer.SFTG.repository;

import com.securetransfer.SFTG.model.DownloadLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DownloadLogRepository extends JpaRepository<DownloadLog, Long> {
    List<DownloadLog> findBySharedLink_Id(Long linkId);
}
