package com.findit.domain;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "report_status_history", indexes = @Index(name = "idx_report_status_history_report", columnList = "report_id,created_at"))
public class ReportStatusHistory {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private AppUser actor;
    @Column(name = "old_status", nullable = false, length = 30)
    private String oldStatus;
    @Column(name = "new_status", nullable = false, length = 30)
    private String newStatus;
    @Column(length = 1000)
    private String reason;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = Instant.now();
    }
    public void setReport(Report report) { this.report = report; }
    public void setActor(AppUser actor) { this.actor = actor; }
    public void setOldStatus(String oldStatus) { this.oldStatus = oldStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
    public void setReason(String reason) { this.reason = reason; }
}
