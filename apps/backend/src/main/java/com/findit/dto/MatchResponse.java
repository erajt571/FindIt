package com.findit.dto;

import com.findit.domain.MatchRecord;
import com.findit.domain.MatchState;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class MatchResponse {
    private final UUID id;
    private final ReportResponse lostReport;
    private final ReportResponse foundReport;
    private final BigDecimal score;
    private final String explanation;
    private final String scoreVersion;
    private final MatchState state;
    private final UUID verificationRequestedById;
    private final Instant createdAt;

    public MatchResponse(MatchRecord match) {
        id = match.getId();
        lostReport = new ReportResponse(match.getReport());
        foundReport = new ReportResponse(match.getCandidateReport());
        score = match.getScore();
        explanation = match.getExplanation();
        scoreVersion = match.getScoreVersion();
        state = match.getState();
        verificationRequestedById = match.getVerificationRequestedBy() == null
                ? null : match.getVerificationRequestedBy().getId();
        createdAt = match.getCreatedAt();
    }
    public UUID getId() { return id; }
    public ReportResponse getLostReport() { return lostReport; }
    public ReportResponse getFoundReport() { return foundReport; }
    public BigDecimal getScore() { return score; }
    public String getExplanation() { return explanation; }
    public String getScoreVersion() { return scoreVersion; }
    public MatchState getState() { return state; }
    public UUID getVerificationRequestedById() { return verificationRequestedById; }
    public Instant getCreatedAt() { return createdAt; }
}
