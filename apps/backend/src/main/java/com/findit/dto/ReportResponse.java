package com.findit.dto;

import com.findit.domain.Report;
import com.findit.domain.ReportStatus;
import com.findit.domain.ReportType;
import java.time.Instant;
import java.util.UUID;

public class ReportResponse {
    private final UUID id;
    private final UUID ownerId;
    private final String ownerDisplayName;
    private final ReportType reportType;
    private final String itemName;
    private final String category;
    private final String description;
    private final String distinguishingDetails;
    private final String locationName;
    private final Instant incidentDate;
    private final String imageUrl;
    private final ReportStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;

    public ReportResponse(Report report) {
        this(report, false);
    }

    public ReportResponse(Report report, boolean includePrivateDetails) {
        id = report.getId();
        ownerId = report.getOwner().getId();
        ownerDisplayName = report.getOwner().getDisplayName();
        reportType = report.getReportType();
        itemName = report.getItemName();
        category = report.getCategory();
        description = report.getDescription();
        distinguishingDetails = includePrivateDetails ? report.getDistinguishingDetails() : null;
        locationName = report.getLocationName();
        incidentDate = report.getIncidentDate();
        imageUrl = report.getImageUrl();
        status = report.getStatus();
        createdAt = report.getCreatedAt();
        updatedAt = report.getUpdatedAt();
    }

    public UUID getId() { return id; }
    public UUID getOwnerId() { return ownerId; }
    public String getOwnerDisplayName() { return ownerDisplayName; }
    public ReportType getReportType() { return reportType; }
    public String getItemName() { return itemName; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getDistinguishingDetails() { return distinguishingDetails; }
    public String getLocationName() { return locationName; }
    public Instant getIncidentDate() { return incidentDate; }
    public String getImageUrl() { return imageUrl; }
    public ReportStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
