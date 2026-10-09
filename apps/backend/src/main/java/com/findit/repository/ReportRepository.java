package com.findit.repository;

import com.findit.domain.Report;
import com.findit.domain.ReportStatus;
import com.findit.domain.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.time.Instant;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {
    Page<Report> findAllByOwner_IdOrderByCreatedAtDesc(UUID ownerId, Pageable pageable);
    Page<Report> findAllByStatusInOrderByCreatedAtDesc(List<ReportStatus> statuses, Pageable pageable);
    long countByOwner_IdAndStatus(UUID ownerId, ReportStatus status);
    long countByStatus(ReportStatus status);

    List<Report> findTop250ByStatusAndReportTypeOrderByCreatedAtDesc(ReportStatus status, ReportType reportType);

    @Query("select r from Report r where r.status = :status "
            + "and (:type is null or r.reportType = :type) "
            + "and (:category is null or lower(r.category) = lower(:category)) "
            + "and (:location is null or lower(r.locationName) like lower(concat('%', :location, '%'))) "
            + "and (:dateFrom is null or r.incidentDate >= :dateFrom) "
            + "and (:dateTo is null or r.incidentDate < :dateTo) "
            + "and (:query is null or lower(concat(concat(concat(concat(r.itemName, ' '), r.category), ' '), concat(r.description, concat(' ', r.locationName)))) like lower(concat('%', :query, '%')))")
    Page<Report> searchActive(@Param("status") ReportStatus status,
                              @Param("type") ReportType type,
                              @Param("category") String category,
                              @Param("location") String location,
                              @Param("dateFrom") Instant dateFrom,
                              @Param("dateTo") Instant dateTo,
                              @Param("query") String query,
                              Pageable pageable);
}
