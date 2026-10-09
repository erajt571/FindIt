package com.findit.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "match_records", uniqueConstraints = {
        @UniqueConstraint(name = "uk_match_pair", columnNames = {"report_id", "candidate_report_id"})
}, indexes = {
        @Index(name = "idx_match_records_report_id", columnList = "report_id"),
        @Index(name = "idx_match_records_state", columnList = "state")
})
public class MatchRecord {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_report_id", nullable = false)
    private Report candidateReport;

    @Column(name = "score", nullable = false, precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 30)
    private MatchState state = MatchState.SUGGESTED;

    @Column(name = "score_version", nullable = false, length = 40)
    private String scoreVersion = "weighted-v1";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verification_requested_by")
    private AppUser verificationRequestedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public MatchRecord() {
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public Report getCandidateReport() {
        return candidateReport;
    }

    public void setCandidateReport(Report candidateReport) {
        this.candidateReport = candidateReport;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public MatchState getState() {
        return state;
    }

    public void setState(MatchState state) {
        this.state = state;
    }

    public String getScoreVersion() {
        return scoreVersion;
    }

    public void setScoreVersion(String scoreVersion) {
        this.scoreVersion = scoreVersion;
    }

    public AppUser getVerificationRequestedBy() {
        return verificationRequestedBy;
    }

    public void setVerificationRequestedBy(AppUser verificationRequestedBy) {
        this.verificationRequestedBy = verificationRequestedBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
