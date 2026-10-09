package com.findit.service;

import com.findit.api.ApiException;
import com.findit.domain.*;
import com.findit.dto.MatchFeedbackRequest;
import com.findit.repository.MatchFeedbackRepository;
import com.findit.repository.MatchRecordRepository;
import com.findit.repository.NotificationRepository;
import com.findit.repository.ReportStatusHistoryRepository;
import com.findit.repository.ReportRepository;
import com.findit.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class MatchFlowService {
    private final MatchRecordRepository matchRepository;
    private final MatchFeedbackRepository feedbackRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ReportStatusHistoryRepository statusHistoryRepository;
    private final ReportRepository reportRepository;

    public MatchFlowService(MatchRecordRepository matchRepository, MatchFeedbackRepository feedbackRepository,
                           NotificationRepository notificationRepository, UserRepository userRepository,
                           ReportStatusHistoryRepository statusHistoryRepository, ReportRepository reportRepository) {
        this.matchRepository = matchRepository;
        this.feedbackRepository = feedbackRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.reportRepository = reportRepository;
    }

    @Transactional(readOnly = true)
    public List<MatchRecord> listForReport(UUID reportId, String email) {
        List<MatchRecord> matches = matchRepository.findAllForReportRanked(reportId);
        if (matches.isEmpty()) {
            Report report = reportRepository.findById(reportId)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Report not found"));
            requireParticipant(report, email);
            return matches;
        }
        Report target = matches.get(0).getReport().getId().equals(reportId)
                ? matches.get(0).getReport() : matches.get(0).getCandidateReport();
        requireParticipant(target, email);
        return matches;
    }

    @Transactional(readOnly = true)
    public MatchRecord get(UUID matchId, String email) {
        MatchRecord match = findMatch(matchId);
        requireParticipant(match, email);
        return match;
    }

    @Transactional
    public MatchRecord transition(UUID matchId, MatchState next, String email) {
        MatchRecord match = findMatch(matchId);
        AppUser actor = requireParticipant(match, email);
        if (next == MatchState.ACCEPTED_FOR_VERIFICATION) {
            if (match.getState() != MatchState.SUGGESTED) {
                throw new ApiException(HttpStatus.CONFLICT, "Only suggested matches can be verified");
            }
            if (match.getReport().getStatus() != ReportStatus.ACTIVE
                    || match.getCandidateReport().getStatus() != ReportStatus.ACTIVE) {
                throw new ApiException(HttpStatus.CONFLICT, "Both reports must be active to request verification");
            }
            match.setState(next);
            match.setVerificationRequestedBy(actor);
            setReportStatus(match.getReport(), actor, ReportStatus.PENDING_VERIFICATION, "Verification requested");
            setReportStatus(match.getCandidateReport(), actor, ReportStatus.PENDING_VERIFICATION, "Verification requested");
            AppUser other = match.getReport().getOwner().getId().equals(actor.getId())
                    ? match.getCandidateReport().getOwner() : match.getReport().getOwner();
            notifyVerification(match, other);
            return match;
        }
        if (next == MatchState.REJECTED
                && (match.getState() == MatchState.SUGGESTED
                || match.getState() == MatchState.ACCEPTED_FOR_VERIFICATION)) {
            match.setState(next);
            if (match.getReport().getStatus() == ReportStatus.PENDING_VERIFICATION)
                setReportStatus(match.getReport(), actor, ReportStatus.ACTIVE, "Verification request declined or cancelled");
            if (match.getCandidateReport().getStatus() == ReportStatus.PENDING_VERIFICATION)
                setReportStatus(match.getCandidateReport(), actor, ReportStatus.ACTIVE, "Verification request declined or cancelled");
            return match;
        }
        if (next == MatchState.CLOSED && match.getState() == MatchState.ACCEPTED_FOR_VERIFICATION) {
            if (match.getVerificationRequestedBy() != null
                    && match.getVerificationRequestedBy().getId().equals(actor.getId())) {
                throw new ApiException(HttpStatus.CONFLICT, "The other report owner must confirm the recovery");
            }
            match.setState(next);
            if (match.getReport().getStatus() == ReportStatus.PENDING_VERIFICATION)
                setReportStatus(match.getReport(), actor, ReportStatus.RESOLVED, "Recovery confirmed by both report owners");
            if (match.getCandidateReport().getStatus() == ReportStatus.PENDING_VERIFICATION)
                setReportStatus(match.getCandidateReport(), actor, ReportStatus.RESOLVED, "Recovery confirmed by both report owners");
            return match;
        }
        throw new ApiException(HttpStatus.CONFLICT, "Invalid match state transition");
    }

    @Transactional
    public void feedback(UUID matchId, MatchFeedbackRequest.Feedback value, String email) {
        MatchRecord match = findMatch(matchId);
        AppUser user = requireParticipant(match, email);
        if (feedbackRepository.existsByMatch_IdAndUser_Id(matchId, user.getId())) {
            throw new ApiException(HttpStatus.CONFLICT, "Feedback has already been submitted");
        }
        MatchFeedback feedback = new MatchFeedback();
        feedback.setMatch(match);
        feedback.setUser(user);
        feedback.setFeedback(value.name());
        feedbackRepository.save(feedback);
    }

    private MatchRecord findMatch(UUID id) {
        return matchRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Match not found"));
    }

    private AppUser requireParticipant(MatchRecord match, String email) {
        AppUser user = requireUser(email);
        UUID userId = user.getId();
        if (!match.getReport().getOwner().getId().equals(userId)
                && !match.getCandidateReport().getOwner().getId().equals(userId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Match not found");
        }
        return user;
    }

    private AppUser requireParticipant(Report report, String email) {
        AppUser user = requireUser(email);
        if (!report.getOwner().getId().equals(user.getId())) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Report not found");
        }
        return user;
    }

    private AppUser requireUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));
    }

    private void notifyVerification(MatchRecord match, AppUser recipient) {
        String key = "verification:" + match.getId() + ":user:" + recipient.getId();
        if (notificationRepository.findByDeduplicationKey(key).isPresent()) return;
        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setTitle("Verification requested");
        notification.setType("VERIFICATION_REQUEST");
        notification.setMessage("Another report owner accepted a possible match. Coordinate verification without sharing private proof publicly.");
        notification.setRelatedReport(match.getReport());
        notification.setMatch(match);
        notification.setDeduplicationKey(key);
        notificationRepository.save(notification);
    }

    private void setReportStatus(Report report, AppUser actor, ReportStatus next, String reason) {
        ReportStatus previous = report.getStatus();
        if (previous == next) return;
        report.setStatus(next);
        ReportStatusHistory history = new ReportStatusHistory();
        history.setReport(report);
        history.setActor(actor);
        history.setOldStatus(previous.name());
        history.setNewStatus(next.name());
        history.setReason(reason);
        statusHistoryRepository.save(history);
    }
}
