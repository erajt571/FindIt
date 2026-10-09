package com.findit.service;

import com.findit.domain.*;
import com.findit.repository.MatchRecordRepository;
import com.findit.repository.ReportRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class MatchingService {
    private final ReportRepository reportRepository;
    private final MatchRecordRepository matchRecordRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final TextSimilarityProvider similarityProvider;
    private final int dateWindowDays;
    private final double minimumScore;

    public MatchingService(ReportRepository reportRepository, MatchRecordRepository matchRecordRepository,
                           ApplicationEventPublisher eventPublisher, TextSimilarityProvider similarityProvider,
                           @Value("${findit.match.date-window-days:90}") int dateWindowDays,
                           @Value("${findit.match.threshold:45}") double minimumScore) {
        this.reportRepository = reportRepository;
        this.matchRecordRepository = matchRecordRepository;
        this.eventPublisher = eventPublisher;
        this.similarityProvider = similarityProvider;
        this.dateWindowDays = dateWindowDays;
        this.minimumScore = minimumScore;
    }

    @Transactional
    public void generateFor(Report report) {
        if (report.getStatus() != ReportStatus.ACTIVE) return;
        ReportType opposite = report.getReportType() == ReportType.LOST ? ReportType.FOUND : ReportType.LOST;
        List<Report> candidates = reportRepository.findTop250ByStatusAndReportTypeOrderByCreatedAtDesc(
                ReportStatus.ACTIVE, opposite);
        for (Report candidate : candidates) {
            if (report.getId().equals(candidate.getId())
                    || report.getOwner().getId().equals(candidate.getOwner().getId())
                    || !withinDateWindow(report, candidate)) continue;
            Report lost = report.getReportType() == ReportType.LOST ? report : candidate;
            Report found = report.getReportType() == ReportType.FOUND ? report : candidate;
            if (matchRecordRepository.findByReport_IdAndCandidateReport_Id(lost.getId(), found.getId()).isPresent()) continue;
            Score score = score(lost, found);
            if (score.value < minimumScore) continue;
            MatchRecord match = new MatchRecord();
            match.setReport(lost);
            match.setCandidateReport(found);
            match.setScore(BigDecimal.valueOf(score.value).setScale(2, RoundingMode.HALF_UP));
            match.setExplanation(score.explanation);
            match.setState(MatchState.SUGGESTED);
            match.setScoreVersion("weighted-v1-local");
            MatchRecord saved = matchRecordRepository.save(match);
            publishAfterCommit(saved.getId());
        }
    }

    private boolean withinDateWindow(Report first, Report second) {
        Instant firstDate = first.getIncidentDate();
        Instant secondDate = second.getIncidentDate();
        if (firstDate == null || secondDate == null) return true;
        return Math.abs(Duration.between(firstDate, secondDate).toDays()) <= dateWindowDays;
    }

    private Score score(Report lost, Report found) {
        double semantic = similarityProvider.similarity(text(lost), text(found));
        double category = lost.getCategory().equalsIgnoreCase(found.getCategory()) ? 1.0 : 0.0;
        double location = lost.getLocationName().equalsIgnoreCase(found.getLocationName()) ? 1.0 : 0.0;
        double date = 0.5;
        if (lost.getIncidentDate() != null && found.getIncidentDate() != null) {
            long delta = Math.abs(Duration.between(lost.getIncidentDate(), found.getIncidentDate()).toDays());
            date = Math.max(0.0, 1.0 - (double) delta / Math.max(1, dateWindowDays));
        }
        double result = (0.60 * semantic + 0.20 * category + 0.10 * location + 0.10 * date) * 100.0;
        String explanation = String.format("Text overlap %.0f%%; %s category; %s location; date proximity %.0f%%. Suggestions are not proof of ownership.",
                semantic * 100.0, category == 1.0 ? "same" : "different", location == 1.0 ? "same" : "different", date * 100.0);
        return new Score(result, explanation);
    }

    private String text(Report report) {
        return report.getItemName() + " " + report.getCategory() + " " + report.getDescription()
                + " " + (report.getDistinguishingDetails() == null ? "" : report.getDistinguishingDetails());
    }

    private void publishAfterCommit(java.util.UUID matchId) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    eventPublisher.publishEvent(new MatchCreatedEvent(matchId));
                }
            });
        } else {
            eventPublisher.publishEvent(new MatchCreatedEvent(matchId));
        }
    }

    private static class Score {
        private final double value;
        private final String explanation;
        private Score(double value, String explanation) {
            this.value = value;
            this.explanation = explanation;
        }
    }
}
