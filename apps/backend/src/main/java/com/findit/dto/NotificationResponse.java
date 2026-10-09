package com.findit.dto;

import com.findit.domain.Notification;
import java.time.Instant;
import java.util.UUID;

public class NotificationResponse {
    private final UUID id;
    private final String title;
    private final String type;
    private final String message;
    private final UUID relatedReportId;
    private final UUID matchId;
    private final Instant readAt;
    private final Instant createdAt;

    public NotificationResponse(Notification notification) {
        id = notification.getId();
        title = notification.getTitle();
        type = notification.getType();
        message = notification.getMessage();
        relatedReportId = notification.getRelatedReport() == null ? null : notification.getRelatedReport().getId();
        matchId = notification.getMatch() == null ? null : notification.getMatch().getId();
        readAt = notification.getReadAt();
        createdAt = notification.getCreatedAt();
    }
    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public String getMessage() { return message; }
    public UUID getRelatedReportId() { return relatedReportId; }
    public UUID getMatchId() { return matchId; }
    public Instant getReadAt() { return readAt; }
    public Instant getCreatedAt() { return createdAt; }
}
