package com.findit.repository;

import com.findit.domain.MatchFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface MatchFeedbackRepository extends JpaRepository<MatchFeedback, UUID> {
    boolean existsByMatch_IdAndUser_Id(UUID matchId, UUID userId);
}
