package com.findit.repository;

import com.findit.domain.MatchJob;
import com.findit.domain.MatchJobState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import javax.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MatchJobRepository extends JpaRepository<MatchJob, UUID> {
    List<MatchJob> findTop10ByStateInAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(
            List<MatchJobState> states, Instant now);
    boolean existsByReport_IdAndStateIn(UUID reportId, List<MatchJobState> states);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select j from MatchJob j where j.id = :id")
    java.util.Optional<MatchJob> findByIdForUpdate(@Param("id") UUID id);
}
