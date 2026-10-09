package com.findit.service;

import com.findit.api.ApiException;
import com.findit.domain.*;
import com.findit.dto.ReportRequest;
import com.findit.repository.MatchJobRepository;
import com.findit.repository.MatchRecordRepository;
import com.findit.repository.ReportStatusHistoryRepository;
import com.findit.repository.ReportRepository;
import com.findit.repository.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.time.Instant;

@Service
public class ReportService {
    private static final List<MatchJobState> OPEN_JOBS = Arrays.asList(
            MatchJobState.PENDING, MatchJobState.RUNNING, MatchJobState.RETRY);

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final MatchJobRepository matchJobRepository;
    private final MatchRecordRepository matchRecordRepository;
    private final ReportStatusHistoryRepository statusHistoryRepository;

    public ReportService(ReportRepository reportRepository, UserRepository userRepository,
                        MatchJobRepository matchJobRepository, MatchRecordRepository matchRecordRepository,
                        ReportStatusHistoryRepository statusHistoryRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.matchJobRepository = matchJobRepository;
        this.matchRecordRepository = matchRecordRepository;
        this.statusHistoryRepository = statusHistoryRepository;
    }

    @Transactional(readOnly = true)
    public Page<Report> browse(ReportType type, String category, String location, Instant dateFrom, Instant dateTo, String query,
                               int page, int size, String sort) {
        if (dateFrom != null && dateTo != null && !dateFrom.isBefore(dateTo)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "dateFrom must be earlier than dateTo");
        }
        Sort primarySort = Sort.by("itemName".equals(sort) ? Sort.Direction.ASC : Sort.Direction.DESC,
                "itemName".equals(sort) ? "itemName" : "createdAt");
        Pageable pageable = PageRequest.of(page, size, primarySort.and(Sort.by(
                "itemName".equals(sort) ? Sort.Direction.ASC : Sort.Direction.DESC, "id")));
        return reportRepository.searchActive(ReportStatus.ACTIVE, type, blankToNull(category),
                blankToNull(location), dateFrom, dateTo, blankToNull(query), pageable);
    }

    @Transactional(readOnly = true)
    public Page<Report> mine(String email, int page, int size) {
        AppUser user = findUser(email);
        return reportRepository.findAllByOwner_IdOrderByCreatedAtDesc(user.getId(), PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public Report get(UUID id, String email, boolean admin) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Report not found"));
        boolean owner = report.getOwner().getEmail().equals(email);
        if (report.getStatus() != ReportStatus.ACTIVE && !owner && !admin) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Report not found");
        }
        return report;
    }

    @Transactional
    public Report create(ReportRequest request, String email) {
        Report report = new Report();
        report.setOwner(findUser(email));
        apply(request, report);
        report.setStatus(ReportStatus.ACTIVE);
        Report saved = reportRepository.save(report);
        recordStatus(saved, saved.getOwner(), null, ReportStatus.ACTIVE, null);
        enqueue(saved);
        return saved;
    }

    @Transactional
    public Report update(UUID id, ReportRequest request, String email, boolean admin) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Report not found"));
        requireOwnerOrAdmin(report, email, admin);
        if (report.getStatus() != ReportStatus.ACTIVE) {
            throw new ApiException(HttpStatus.CONFLICT, "Only active reports can be edited");
        }
        apply(request, report);
        closeSuggestedMatches(report);
        enqueue(report);
        return report;
    }

    @Transactional
    public Report changeStatus(UUID id, ReportStatus next, String email, boolean admin) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Report not found"));
        requireOwnerOrAdmin(report, email, admin);
        boolean allowed = report.getStatus() == ReportStatus.ACTIVE && next == ReportStatus.RESOLVED;
        if (!allowed) {
            throw new ApiException(HttpStatus.CONFLICT, "Use a match verification request to change reports awaiting verification");
        }
        ReportStatus previous = report.getStatus();
        report.setStatus(next);
        recordStatus(report, findUser(email), previous, next, null);
        if (next == ReportStatus.RESOLVED) closeSuggestedMatches(report);
        return report;
    }

    @Transactional(readOnly = true)
    public Report requireReport(UUID id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Report not found"));
    }

    @Transactional
    public void enqueue(Report report) {
        if (report.getStatus() != ReportStatus.ACTIVE
                || matchJobRepository.existsByReport_IdAndStateIn(report.getId(), OPEN_JOBS)) return;
        MatchJob job = new MatchJob();
        job.setReport(report);
        job.setState(MatchJobState.PENDING);
        matchJobRepository.save(job);
    }

    private void apply(ReportRequest request, Report report) {
        report.setReportType(request.getReportType());
        report.setItemName(request.getItemName().trim());
        report.setCategory(request.getCategory().trim());
        report.setDescription(request.getDescription().trim());
        report.setDistinguishingDetails(trimToNull(request.getDistinguishingDetails()));
        report.setLocationName(request.getLocationName().trim());
        report.setIncidentDate(request.getIncidentDate());
        report.setImageUrl(trimToNull(request.getImageUrl()));
    }

    private void requireOwnerOrAdmin(Report report, String email, boolean admin) {
        if (!admin && !report.getOwner().getEmail().equals(email)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Report not found");
        }
    }

    private void closeSuggestedMatches(Report report) {
        for (MatchRecord match : matchRecordRepository.findAllForReport(report.getId())) {
            if (match.getState() == MatchState.SUGGESTED) match.setState(MatchState.CLOSED);
        }
    }

    public void recordStatus(Report report, AppUser actor, ReportStatus previous, ReportStatus next, String reason) {
        ReportStatusHistory history = new ReportStatusHistory();
        history.setReport(report);
        history.setActor(actor);
        history.setOldStatus(previous == null ? "CREATED" : previous.name());
        history.setNewStatus(next.name());
        history.setReason(reason);
        statusHistoryRepository.save(history);
    }

    private AppUser findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));
    }

    private String blankToNull(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private String trimToNull(String value) {
        return blankToNull(value);
    }
}
