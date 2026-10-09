package com.findit.repository;

import com.findit.domain.MatchRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MatchRecordRepository extends JpaRepository<MatchRecord, UUID> {
    Optional<MatchRecord> findByReport_IdAndCandidateReport_Id(UUID reportId, UUID candidateReportId);

    @Query("select m from MatchRecord m where m.report.id = :reportId or m.candidateReport.id = :reportId order by m.createdAt desc")
    List<MatchRecord> findAllForReport(@Param("reportId") UUID reportId);

    @Query("select m from MatchRecord m where m.report.id = :reportId or m.candidateReport.id = :reportId order by m.score desc")
    List<MatchRecord> findAllForReportRanked(@Param("reportId") UUID reportId);

    @Query("select count(m) from MatchRecord m where (m.report.owner.id = :userId or m.candidateReport.owner.id = :userId) and m.state = com.findit.domain.MatchState.SUGGESTED")
    long countSuggestedForUser(@Param("userId") UUID userId);
}
