package com.findit.dto;

import com.findit.domain.ModerationAction;
import java.time.Instant;
import java.util.UUID;

public class ModerationActionResponse {
    private final UUID id;
    private final UUID adminId;
    private final String adminName;
    private final String targetType;
    private final UUID targetId;
    private final String action;
    private final String reason;
    private final Instant createdAt;
    public ModerationActionResponse(ModerationAction action) {
        id = action.getId();
        adminId = action.getAdmin().getId();
        adminName = action.getAdmin().getDisplayName();
        targetType = action.getTargetType();
        targetId = action.getTargetId();
        this.action = action.getAction();
        reason = action.getReason();
        createdAt = action.getCreatedAt();
    }
    public UUID getId() { return id; }
    public UUID getAdminId() { return adminId; }
    public String getAdminName() { return adminName; }
    public String getTargetType() { return targetType; }
    public UUID getTargetId() { return targetId; }
    public String getAction() { return action; }
    public String getReason() { return reason; }
    public Instant getCreatedAt() { return createdAt; }
}
