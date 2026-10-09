package com.findit.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ModerationRequest {
    @NotBlank
    @Size(min = 5, max = 1000)
    private String reason;
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
