package com.findit.dto;

import javax.validation.constraints.NotNull;

public class MatchFeedbackRequest {
    @NotNull
    private Feedback feedback;
    public Feedback getFeedback() { return feedback; }
    public void setFeedback(Feedback feedback) { this.feedback = feedback; }
    public enum Feedback { RELEVANT, NOT_RELEVANT }
}
