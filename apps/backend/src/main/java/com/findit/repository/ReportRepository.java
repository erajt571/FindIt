package com.findit.repository;

import com.findit.domain.Report;
import com.findit.domain.ReportStatus;
import com.findit.domain.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID>, JpaSpecificationExecutor<Report> {
    Page<Report> findAllByOwner_IdOrderByCreatedAtDesc(UUID ownerId, Pageable pageable);
    Page<Report> findAllByStatusInOrderByCreatedAtDesc(List<ReportStatus> statuses, Pageable pageable);
    long countByOwner_IdAndStatus(UUID ownerId, ReportStatus status);
    long countByStatus(ReportStatus status);

    List<Report> findTop250ByStatusAndReportTypeOrderByCreatedAtDesc(ReportStatus status, ReportType reportType);

}
