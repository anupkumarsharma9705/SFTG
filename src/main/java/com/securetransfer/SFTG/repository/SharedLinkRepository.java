package com.securetransfer.SFTG.repository;

import com.securetransfer.SFTG.model.SharedLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SharedLinkRepository extends JpaRepository<SharedLink, Long> {

    Optional<SharedLink> findByTokenAndActiveTrue(String token);
}
