package com.findit.repository;

import com.findit.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findAllByUser_IdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    long countByUser_IdAndReadAtIsNull(UUID userId);
    Optional<Notification> findByDeduplicationKey(String deduplicationKey);
    Optional<Notification> findByIdAndUser_Id(UUID id, UUID userId);
}
