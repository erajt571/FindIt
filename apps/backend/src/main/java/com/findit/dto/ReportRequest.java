package com.findit.dto;

import com.findit.domain.ReportType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;

public class ReportRequest {
    @NotNull
    private ReportType reportType;
    @NotBlank @Size(max = 120)
    private String itemName;
    @NotBlank @Size(max = 80)
    private String category;
    @NotBlank @Size(max = 5000)
    private String description;
    @Size(max = 2000)
    private String distinguishingDetails;
    @NotBlank @Size(max = 120)
    private String locationName;
    private Instant incidentDate;
    @Size(max = 1000)
    private String imageUrl;

    public ReportType getReportType() { return reportType; }
    public void setReportType(ReportType reportType) { this.reportType = reportType; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDistinguishingDetails() { return distinguishingDetails; }
    public void setDistinguishingDetails(String distinguishingDetails) { this.distinguishingDetails = distinguishingDetails; }
    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public Instant getIncidentDate() { return incidentDate; }
    public void setIncidentDate(Instant incidentDate) { this.incidentDate = incidentDate; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
