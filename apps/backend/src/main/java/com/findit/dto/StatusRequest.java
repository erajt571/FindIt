package com.findit.dto;

import com.findit.domain.ReportStatus;
import javax.validation.constraints.NotNull;

public class StatusRequest {
    @NotNull
    private ReportStatus status;
    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }
}
