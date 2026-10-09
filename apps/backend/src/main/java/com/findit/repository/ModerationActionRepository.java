package com.findit.repository;

import com.findit.domain.ModerationAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface ModerationActionRepository extends JpaRepository<ModerationAction, UUID> {
    Page<ModerationAction> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
