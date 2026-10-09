package com.findit.service;

import com.findit.api.ApiException;
import com.findit.domain.*;
import com.findit.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.UUID;

@Service
public class ModerationService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final MatchRecordRepository matchRepository;
    private final ModerationActionRepository actionRepository;
    private final MatchJobRepository jobRepository;
    private final ReportService reportService;
    public ModerationService(ReportRepository reportRepository, UserRepository userRepository,
                             MatchRecordRepository matchRepository, ModerationActionRepository actionRepository,
                             MatchJobRepository jobRepository, ReportService reportService) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.matchRepository = matchRepository;
        this.actionRepository = actionRepository;
        this.jobRepository = jobRepository;
        this.reportService = reportService;
    }

    @Transactional(readOnly = true)
    public Page<Report> queue(int page, int size) {
        return reportRepository.findAllByStatusInOrderByCreatedAtDesc(
                Arrays.asList(ReportStatus.ACTIVE, ReportStatus.PENDING_VERIFICATION), PageRequest.of(page, size));
    }

    @Transactional
    public void removeReport(UUID reportId, String adminEmail, String reason) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Report not found"));
        if (report.getStatus() == ReportStatus.REMOVED)
            throw new ApiException(HttpStatus.CONFLICT, "Report is already removed");
        AppUser admin = findAdmin(adminEmail);
        ReportStatus previous = report.getStatus();
        report.setStatus(ReportStatus.REMOVED);
        reportService.recordStatus(report, admin, previous, ReportStatus.REMOVED, reason.trim());
        for (MatchRecord match : matchRepository.findAllForReport(reportId)) {
            if (match.getState() == MatchState.SUGGESTED
                    || match.getState() == MatchState.ACCEPTED_FOR_VERIFICATION) {
                match.setState(MatchState.CLOSED);
                Report other = match.getReport().getId().equals(reportId)
                        ? match.getCandidateReport() : match.getReport();
                if (other.getStatus() == ReportStatus.PENDING_VERIFICATION) {
                    other.setStatus(ReportStatus.ACTIVE);
                    reportService.recordStatus(other, admin, ReportStatus.PENDING_VERIFICATION,
                            ReportStatus.ACTIVE, "Related report removed during moderation");
                }
            }
        }
        action("REPORT", reportId, "REMOVE", adminEmail, reason);
    }

    @Transactional
    public void suspendUser(UUID userId, String adminEmail, String reason) {
        AppUser target = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        AppUser admin = findAdmin(adminEmail);
        if (admin.getId().equals(target.getId()))
            throw new ApiException(HttpStatus.CONFLICT, "Administrators cannot suspend their own account");
        if (target.getAccountStatus() == AccountStatus.SUSPENDED)
            throw new ApiException(HttpStatus.CONFLICT, "User is already suspended");
        target.setAccountStatus(AccountStatus.SUSPENDED);
        action("USER", userId, "SUSPEND", adminEmail, reason);
    }

    @Transactional(readOnly = true)
    public Page<ModerationAction> actions(int page, int size) {
        return actionRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
    }

    private AppUser findAdmin(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Authenticated administrator not found"));
    }

    private void action(String type, UUID targetId, String action, String adminEmail, String reason) {
        ModerationAction audit = new ModerationAction();
        audit.setAdmin(findAdmin(adminEmail));
        audit.setTargetType(type);
        audit.setTargetId(targetId);
        audit.setAction(action);
        audit.setReason(reason.trim());
        actionRepository.save(audit);
    }
}
